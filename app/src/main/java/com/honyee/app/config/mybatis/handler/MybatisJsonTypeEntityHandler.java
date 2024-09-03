package com.honyee.app.config.mybatis.handler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.honyee.app.config.cache.MyJavaTimeModule;
import com.honyee.app.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;

@Slf4j
@MappedTypes(Object.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class MybatisJsonTypeEntityHandler<T> extends AbstractJsonTypeHandler<T> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TypeReference<T> typeReference;

    private final Class<T> clz;

    private final boolean isObject;

    static {
        // 日期类处理
        MAPPER.registerModule(new MyJavaTimeModule());
        // 未知字段忽略
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 不使用科学计数
        MAPPER.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        // null 值不输出(节省内存)
        MAPPER.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        // 序列化时输出 @class
        MAPPER.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
    }

    /**
     * 单个对象时（参考People中的role字段）
     */
    public MybatisJsonTypeEntityHandler(Class<T> type) {
        this.isObject = true;
        this.clz = type;
        this.typeReference = null;
    }

    /**
     * 数组对象时（参考People中的roleList字段）
     */
    public MybatisJsonTypeEntityHandler(Class<T> type, TypeReference<T> typeReference) {
        this.isObject = false;
        this.clz = type;
        this.typeReference = typeReference;
    }

    @Override
    protected T parse(String json) {
        try {
            if (this.isObject) {
                return MAPPER.readValue(json, clz);
            }
            return MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("parse failed\njson={}\n{}", json, LogUtil.filterStackToString(e));
            return null;
        }
    }

    @Override
    protected String toJson(T obj) {
        try {
            if (this.isObject) {
                return MAPPER.writeValueAsString(obj);
            }
            return MAPPER.writerFor(typeReference).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("toJson failed\nobj={}\n{}", obj, LogUtil.filterStackToString(e));
            return null;
        }
    }

}
