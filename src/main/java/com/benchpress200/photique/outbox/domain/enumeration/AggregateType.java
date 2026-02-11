package com.benchpress200.photique.outbox.domain.enumeration;

import lombok.Getter;

@Getter
public enum AggregateType {
    SINGLEWORK("singlework"),
    EXHIBITION("exhibition"),
    FOLLOW("follow"),
    USER("user");

    private final String value;

    AggregateType(String value) {
        this.value = value;
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
