package com.honyee.app.config.mybatis.handler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.config.cache.MyJavaTimeModule;
import com.honyee.app.exp.CommonException;
import com.honyee.app.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 处理Entity类型的字段
 */
public class MybatisJsonTypeEntityHandler<T> extends AbstractJsonTypeHandler<T> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Class<T> clz;

    public MybatisJsonTypeEntityHandler(Class<T> clz) {
        this.clz = clz;
    }

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


    @Override
    protected T parse(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, this.clz);
        } catch (JsonProcessingException e) {
            LogUtil.error("parse failed\njson={}\n{}", json, LogUtil.filterStackToString(e));
            return null;
        }
    }

    @Override
    protected String toJson(T obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new CommonException(e);
        }
    }

}
