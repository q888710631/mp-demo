package com.honyee.app.config.objectmapper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.honyee.app.config.cache.MyJavaTimeModule;
import com.honyee.app.utils.DateUtil;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return createObjectMapper(builder);
    }

    //@Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.deserializers(new LocalDateTimeDeserializer(DateUtil.COMMON_DATE_TIME_FORMATTER));
            builder.serializers(new LocalDateTimeSerializer(DateUtil.COMMON_DATE_TIME_FORMATTER));
        };
    }

    public static ObjectMapper createObjectMapper(Jackson2ObjectMapperBuilder builder) {
        JsonFactory jsonFactory = new JsonFactoryBuilder().enable(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS).build();
        ObjectMapper objectMapper = builder.createXmlMapper(false).factory(jsonFactory).build();
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        // 尝试序列化所有属性，即使是private
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 解决日期类无法序列化问题
        objectMapper.registerModule(new MyJavaTimeModule());
        return objectMapper;
    }

}
