package com.honyee.app.config.mybatis.handler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.honyee.app.config.cache.MyJavaTimeModule;
import com.honyee.app.exp.CommonException;
import com.honyee.app.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 处理List类型的字段
 */
public class MybatisJsonTypeListHandler<T> extends AbstractJsonTypeHandler<List<T>> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final CollectionType collectionType;

    static {
        // 日期类处理
        MAPPER.registerModule(new MyJavaTimeModule());
        // 未知字段忽略
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 不使用科学计数
        MAPPER.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        // null 值不输出(节省内存)
        MAPPER.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
//         序列化时输出 @class
//        MAPPER.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
//            ObjectMapper.DefaultTyping.NON_FINAL,
//            JsonTypeInfo.As.PROPERTY
//        );
    }

    public MybatisJsonTypeListHandler() {
        Class<? extends MybatisJsonTypeListHandler> aClass = getClass();
        Type type = aClass.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type[] types = pt.getActualTypeArguments();
            if (types != null && types.length > 0) {
                Class<T> clz = (Class<T>) types[0];
                this.collectionType = MAPPER.getTypeFactory().constructCollectionType(List.class, clz);
                return;
            }
        }
        LogUtil.warn("{} 没有指定泛型，使用默认类型", aClass.getName());
        this.collectionType = MAPPER.getTypeFactory().constructCollectionType(List.class, LinkedHashMap.class);
    }

    @Override
    protected List<T> parse(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, this.collectionType);
        } catch (JsonProcessingException e) {
            LogUtil.error("parse failed, json={}", json, e);
            return null;
        }
    }

    @Override
    protected String toJson(List<T> obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new CommonException(e);
        }
    }

}
