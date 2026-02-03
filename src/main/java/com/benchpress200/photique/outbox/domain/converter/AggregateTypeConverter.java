package com.benchpress200.photique.outbox.domain.converter;

import com.benchpress200.photique.outbox.domain.enumeration.AggregateType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class AggregateTypeConverter implements AttributeConverter<AggregateType, String> {

    @Override
    public String convertToDatabaseColumn(AggregateType attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getValue();
    }

    @Override
    public AggregateType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        return AggregateType.from(dbData);
    }
}
