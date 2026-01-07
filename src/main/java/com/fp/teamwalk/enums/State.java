package com.fp.teamwalk.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum State {
    ENABLED("enabled"),
    DISABLED("disabled");

    private final String value;

    @JsonValue // Jackson 3 uses this for both serialization and deserialization
    public String getValue() {
        return value;
    }
}