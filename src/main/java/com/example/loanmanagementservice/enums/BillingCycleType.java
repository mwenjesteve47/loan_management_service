package com.example.loanmanagementservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BillingCycleType {
    INDIVIDUAL_DUE_DATE("individual"),
    CONSOLIDATED_DUE_DATE("consolidated");

    private final String value;

    BillingCycleType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BillingCycleType fromValue(String value) {
        for (BillingCycleType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid Billing CycleType: " + value);
    }
}
