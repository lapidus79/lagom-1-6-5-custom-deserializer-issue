package com.blah.jacksontest.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import play.libs.Json;

import java.io.IOException;

@SuppressWarnings("WeakerAccess")
public class ChildDeserializer extends StdDeserializer<Child> {

    public ChildDeserializer() {
        this(null);
    }

    public ChildDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Child deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        Json.mapper().getRegisteredModuleIds()
                .stream().forEach(e ->
                System.out.println("--------------- " + e));


        JsonNode node = jp.readValueAsTree(); //(jp);
        String updatedBy = node.get("updatedBy").asText();
        Long updatedAt = node.get("updatedAt").asLong();


        return Child.builder()
                .updatedAt(updatedAt)
                .updatedBy(updatedBy)
                .build();
    }
}
