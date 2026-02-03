package com.benchpress200.photique.outbox.domain.converter;

import com.benchpress200.photique.outbox.domain.enumeration.EventType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EventTypeConverter implements AttributeConverter<EventType, String> {

    @Override
    public String convertToDatabaseColumn(EventType attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getValue();
    }

    @Override
    public EventType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return EventType.from(dbData);
    }
}
