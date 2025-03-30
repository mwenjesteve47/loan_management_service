package com.example.loanmanagementservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TenureUnit {
    DAYS("Days"),
    MONTHS("Months");

    private final String value;

    TenureUnit(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TenureUnit fromValue(String value) {
        for (TenureUnit unit : values()) {
            if (unit.value.equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid TenureUnit: " + value);
    }}
