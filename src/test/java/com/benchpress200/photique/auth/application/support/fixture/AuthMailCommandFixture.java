package com.benchpress200.photique.auth.application.support.fixture;

import com.benchpress200.photique.auth.application.command.model.AuthMailCommand;

public class AuthMailCommandFixture {
    private AuthMailCommandFixture() {
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

        public AuthMailCommand build() {
            return AuthMailCommand.builder()
                    .email(email)
                    .build();
        }
    }
}
