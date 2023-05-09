package com.honyee.app.config.objectmapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.honyee.app.utils.DateUtil;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class InstantJsonSerializer extends JsonSerializer<Instant> {

    @Override
    public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//        jsonGenerator.writeNumber(instant.toEpochMilli());
        String format = DateUtil.COMMON_DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(instant, ZoneId.of("GMT+8")));
        jsonGenerator.writeString(format);
    }

}