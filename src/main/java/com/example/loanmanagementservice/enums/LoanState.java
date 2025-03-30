package com.example.loanmanagementservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LoanState {
    OPEN("OPEN"),
    CLOSED("CLOSED"),
    CANCELLED("CANCELLED"),
    OVERDUE("OVERDUE"),
    WRITTEN_OFF("WRITTEN_OFF");

    private final String value;

    LoanState(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LoanState fromValue(String value) {
        for (LoanState state : values()) {
            if (state.value.equalsIgnoreCase(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid LoanState : " + value);
    }
}
