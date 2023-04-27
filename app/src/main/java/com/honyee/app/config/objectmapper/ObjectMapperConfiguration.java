package com.honyee.app.config.objectmapper;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.cloud.openfeign.support.SortJsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Instant;
import java.time.LocalDateTime;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        JsonFactory jsonFactory = new JsonFactoryBuilder().enable(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS).build();
        ObjectMapper objectMapper = builder.createXmlMapper(false).factory(jsonFactory).build();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Sort.class, new SortJsonComponent.SortSerializer());
        module.addDeserializer(Sort.class, new SortJsonComponent.SortDeserializer());
        module.addSerializer(Instant.class, new InstantJsonSerializer());
        module.addDeserializer(Instant.class, new InstantJsonDeserializer());
        module.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
        module.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        return objectMapper;
    }

}
