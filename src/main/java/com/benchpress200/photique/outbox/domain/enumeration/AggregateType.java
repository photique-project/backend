package com.benchpress200.photique.outbox.domain.enumeration;

public enum AggregateType {
    SINGLEWORK("singlework"),
    EXHIBITION("exhibition"),
    USER("user");

    private final String value;

    AggregateType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AggregateType from(String value) {
        for (AggregateType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unknown AggregateType: " + value);
    }
}
