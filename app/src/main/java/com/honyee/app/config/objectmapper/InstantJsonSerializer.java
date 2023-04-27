package com.honyee.app.config.objectmapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;

public class InstantJsonSerializer extends JsonSerializer<Instant> {

    @Override
    public void serialize(
        Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws IOException {
        jsonGenerator.writeNumber(instant.toEpochMilli());
    }
}