package com.blah.jacksontest.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
@JsonDeserialize(using = ChildDeserializer.class)
public class Child {

    @NonNull Long updatedAt;
    @NonNull String updatedBy;

}
