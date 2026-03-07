package com.benchpress200.photique.support.fixture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class MultipartJsonFixture {
    private MultipartJsonFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String key;
        private Object object;
        private ObjectMapper objectMapper;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder object(Object object) {
            this.object = object;
            return this;
        }

        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public MockMultipartFile build() throws JsonProcessingException {
            return new MockMultipartFile(
                    key,
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(object)
            );
        }
    }
}
