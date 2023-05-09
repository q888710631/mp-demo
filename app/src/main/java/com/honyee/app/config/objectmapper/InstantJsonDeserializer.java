package com.honyee.app.config.objectmapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.honyee.app.utils.DateUtil;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class InstantJsonDeserializer extends JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
//        return Instant.ofEpochMilli(Long.parseLong(jsonParser.getValueAsString()));
        LocalDateTime parse = LocalDateTime.parse(jsonParser.getValueAsString(), DateUtil.COMMON_DATE_TIME_FORMATTER);
        return parse.toInstant(ZoneOffset.UTC);
    }
}
