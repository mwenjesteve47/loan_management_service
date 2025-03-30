package com.example.loanmanagementservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LoanStructure {
    LUMP_SUM("lump_sum"),
    INSTALLMENTS("installments");

    private final String value;

    LoanStructure(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LoanStructure fromValue(String value) {
        for (LoanStructure type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid LoanStructure : " + value);
    }
}
