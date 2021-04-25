package com.blah.jacksontest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import play.libs.Json;

import static com.blah.jacksontest.impl.JacksontestServiceImpl.dummyState;

public class RootEntityStateTest {

    @Test
    public void serializeDeserialize() {
        RootEntityState state = dummyState();
        String str = Json.prettyPrint(Json.toJson(state));
        JsonNode node = Json.parse(str);
        System.out.println("createdAt define in node " + node.get("createdAt"));
        // But after deserialization things turn ugly after jackson? has successfully
        // deserialized the Child object. After that it looks like the usage of a customer
        // deserializer halts the flow and all remaining attributes (that are lockated below child attribute)
        // are ignored and null values are passed instead in the constructor
        // Due to the @Nonnull annotation we then end up with an exception
        RootEntityState result = Json.fromJson(node, RootEntityState.class);
        assert (result).equals(state);
    }

}
