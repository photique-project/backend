package com.benchpress200.photique.auth.api.command.support.fixture;

import com.benchpress200.photique.auth.api.command.request.AuthMailRequest;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AuthMailRequestFixture {
    private AuthMailRequestFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email = "test@example.com";

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public AuthMailRequest build() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            ObjectNode node = objectMapper.createObjectNode();

            if (email != null) {
                node.put("email", email);
            }

            return objectMapper.convertValue(node, AuthMailRequest.class);
        }
    }
}
