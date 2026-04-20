package com.benchpress200.photique.auth.api.command.support.fixture;

import com.benchpress200.photique.auth.api.command.request.AuthMailCodeValidateRequest;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AuthMailCodeValidateRequestFixture {
    private AuthMailCodeValidateRequestFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email = "test@example.com";
        private String code = "123456";

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public AuthMailCodeValidateRequest build() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            ObjectNode node = objectMapper.createObjectNode();

            if (email != null) {
                node.put("email", email);
            }

            if (code != null) {
                node.put("code", code);
            }

            return objectMapper.convertValue(node, AuthMailCodeValidateRequest.class);
        }
    }
}
