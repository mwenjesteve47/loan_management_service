package com.example.loanmanagementservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FeeCalculationType {
    FIXED("Fixed"),
    PERCENTAGE("Percentage");

    private final String value;

    FeeCalculationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FeeCalculationType fromValue(String value) {
        for (FeeCalculationType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid FeeStructureType: " + value);
    }
}
