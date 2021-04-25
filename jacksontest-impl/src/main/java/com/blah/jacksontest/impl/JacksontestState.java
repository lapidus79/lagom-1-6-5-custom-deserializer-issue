package com.blah.jacksontest.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * The state for the {@link JacksontestAggregate} entity.
 */
@SuppressWarnings("serial")
@Value
@JsonDeserialize
public final class JacksontestState implements CompressedJsonable {
    public static final JacksontestState INITIAL = new JacksontestState("Hello", LocalDateTime.now().toString());
    public final String message;
    public final String timestamp;

    @JsonCreator
    JacksontestState(String message, String timestamp) {
        this.message = Preconditions.checkNotNull(message, "message");
        this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
    }

    public JacksontestState withMessage(String message) {
        return new JacksontestState(message, LocalDateTime.now().toString());
    }


}
