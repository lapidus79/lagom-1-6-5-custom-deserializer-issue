package com.blah.jacksontest.impl;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class RootEntityState implements Jsonable {

    @NonNull Long createdAt;
    // We have a child object that uses a custom deserializer
    Child child;
    // after the the custom deserializer we have other fields below.
    // These fields will resolve to NULL values in lagom 1.6.5.
    // Because we have @Nonnull annotations an exception
    // is thrown. Without the NonNull it would just silently set
    // the rest of the fields to null
    @NonNull Long updatedAt;
    @NonNull String updatedBy;

}
