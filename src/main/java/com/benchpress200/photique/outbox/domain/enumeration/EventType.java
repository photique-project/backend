package com.benchpress200.photique.outbox.domain.enumeration;

import lombok.Getter;

@Getter
public enum EventType {
    CREATED("created"),
    UPDATED("updated"),
    DELETED("deleted");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public static EventType from(String value) {
        for (EventType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown EventType: " + value);
    }
}
