package com.honyee.app.service.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.dto.base.MyPage;
import com.honyee.app.enums.OperaTypeEnum;
import com.honyee.app.model.OperaLog;
import com.honyee.app.model.base.BaseEntity;
import com.honyee.app.utils.LogUtil;
import com.honyee.app.utils.TenantHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

/**
 * service增强
 */
@Slf4j
public class MyService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    public <P> IPage<P> toPage(MyPage myPage) {
        return PageDTO.of(myPage.getPage(), myPage.getSize());
    }

    @Resource
    protected ObjectMapper objectMapper;

    @Resource
    protected RedisTemplate<Object, Object> redisTemplate;

    public T findById(Serializable id) {
        return baseMapper.selectById(id);
    }

    public T insertAndLog(T t) {
        return insertAndLog("新增" + getEntityTitle(), t);
    }

    public T insertAndLog(String title, T t) {
        int count = baseMapper.insert(t);
        if (count > 0) {
            saveLog(title, OperaTypeEnum.INSERT, null, t);
        }
        return t;
    }

    public void updateAndLog(T t) {
        updateAndLog("修改" + getEntityTitle(), t);
    }

    public void updateAndLog(String title, T t) {
        T beforeEntity = findById(getEntityId(t));
        int count = baseMapper.updateById(t);
        if (count > 0) {
            saveLog(title, OperaTypeEnum.UPDATE, beforeEntity, t);
        }
    }

    public void deleteAndLog(T t) {
        deleteAndLog("删除" + getEntityTitle(), t);
    }

    public void deleteAndLog(String title, T t) {
        T beforeEntity = findById(getEntityId(t));
        int count = baseMapper.deleteById(t);
        if (count > 0) {
            saveLog(title, OperaTypeEnum.DELETE, beforeEntity, null);
        }
    }
    private void saveLog(String title, OperaTypeEnum operaType, T beforeEntity, T afterEntity) {
        try {
            saveLogDo(title, operaType, beforeEntity, afterEntity);
        } catch (Exception e) {
            log.error("操作日志保存失败", e);
        }
    }

    /**
     * 保存日志
     */
    private void saveLogDo(String title, OperaTypeEnum operaType, T beforeEntity, T afterEntity) throws JsonProcessingException {
        OperaLog operaLog = new OperaLog();
        operaLog.setTitle(title);
        operaLog.setOperaType(operaType);
        operaLog.setCreateDate(LocalDateTime.now());

        // 实体ID
        Serializable entityId;
        switch (operaType) {
            case INSERT:
                entityId = getEntityId(afterEntity);
                break;
            case DELETE:
            case UPDATE:
            default:
                entityId = getEntityId(beforeEntity);
        }
        operaLog.setEntityId(entityId);
        // 请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            ServletRequestAttributes req = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = req.getRequest();
            operaLog.setRequestUrl(request.getRequestURI());
            operaLog.setRequestMethod(request.getMethod());
            Object traceId = request.getAttribute(TraceContext.class.getName());
            if (traceId != null) {
                operaLog.setTraceId(traceId.toString());
            }
        }

        // 租户
        operaLog.setTenantId(TenantHelper.getCurrentUserId());

        // 变动的参数
        Map<String, Pair<Object, Object>> map = compareEntity(beforeEntity, afterEntity);
        Map<String, Object> beforeMap = new HashMap<>();
        Map<String, Object> afterMap = new HashMap<>();
        operaLog.setEntityBefore(beforeMap);
        operaLog.setEntityAfter(afterMap);
        for (Map.Entry<String, Pair<Object, Object>> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            Pair<Object, Object> value = entry.getValue();
            if (operaType != OperaTypeEnum.INSERT) {
                beforeMap.put(fieldName, value.getKey());
            }
            if (operaType != OperaTypeEnum.DELETE) {
                afterMap.put(fieldName, value.getValue());
            }
        }

        // 存储
        String json = objectMapper.writeValueAsString(operaLog);
        redisTemplate.opsForList().leftPush("operaLog", json);
    }

    /**
     * 获取实体名称
     */
    private String getEntityTitle() {
        Class<T> clz = getEntityClass();
        Schema schema = clz.getAnnotation(Schema.class);
        if (schema == null || StringUtil.isBlank(schema.title())) {
            return clz.getName();
        }
        return schema.title();
    }

    /**
     * 获取实体中的id
     */
    private Serializable getEntityId(T t) {
        Class<?> clz = getEntityClass();
        try {
            Field field = clz.getDeclaredField("id");
            field.setAccessible(true);
            Object id = field.get(t);
            if (id != null) {
                return id.toString();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.warn("", e);
        }
        return null;
    }

    /**
     * 忽略对比的字段
     */
    private static final Set<String> IGNORE_FIELD_NAME = new HashSet<>();
    static {
        for (Field declaredField : BaseEntity.class.getDeclaredFields()) {
            IGNORE_FIELD_NAME.add(declaredField.getName());
        }
    }

    /**
     * 比对修改过的字段
     */
    private Map<String, Pair<Object, Object>> compareEntity(T beforeEntity, T afterEntity) {
        Map<String, Pair<Object, Object>> map = new HashMap<>();

        Class<?> clz = getEntityClass();
        for (Field declaredField : clz.getDeclaredFields()) {
            try {
                String fieldName = declaredField.getName();
                if (IGNORE_FIELD_NAME.contains(fieldName)) {
                    continue;
                }
                declaredField.setAccessible(true);
                Object before = beforeEntity == null ? null : declaredField.get(beforeEntity);
                Object after = afterEntity == null ? null : declaredField.get(afterEntity);
                if (!Objects.equals(before, after)) {
                    map.put(fieldName, Pair.create(before, after));
                }
            } catch (IllegalAccessException e) {
                log.warn("", e);
            }
        }

        return map;
    }

}
