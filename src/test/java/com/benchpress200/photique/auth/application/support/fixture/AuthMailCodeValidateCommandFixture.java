package com.benchpress200.photique.auth.application.support.fixture;

import com.benchpress200.photique.auth.application.command.model.AuthMailCodeValidateCommand;

public class AuthMailCodeValidateCommandFixture {
    private AuthMailCodeValidateCommandFixture() {
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

        public AuthMailCodeValidateCommand build() {
            return AuthMailCodeValidateCommand.builder()
                    .email(email)
                    .code(code)
                    .build();
        }
    }
}
