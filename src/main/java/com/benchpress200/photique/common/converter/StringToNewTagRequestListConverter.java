package com.benchpress200.photique.common.converter;

import com.benchpress200.photique.common.domain.dto.NewTagRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToNewTagRequestListConverter implements Converter<String, List<NewTagRequest>> {

    private final ObjectMapper objectMapper;

    public StringToNewTagRequestListConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<NewTagRequest> convert(String source) {
        try {
            return Arrays.asList(objectMapper.readValue(source, NewTagRequest[].class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert String to List<NewTagRequest>", e);
        }
    }
}