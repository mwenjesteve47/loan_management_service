package com.example.loanmanagementservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TenureType {
    FIXED("Fixed"),
    VARIABLE("Variable");

    private final String value;

    TenureType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TenureType fromValue(String value) {
        for (TenureType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid TenureType: " + value);
    }
}
