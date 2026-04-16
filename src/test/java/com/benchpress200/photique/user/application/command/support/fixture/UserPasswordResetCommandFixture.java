package com.benchpress200.photique.user.application.command.support.fixture;

import com.benchpress200.photique.user.application.command.model.UserPasswordResetCommand;

public class UserPasswordResetCommandFixture {
    private UserPasswordResetCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email = "test@example.com";
        private String password = "기본비밀번호1!";

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public UserPasswordResetCommand build() {
            return UserPasswordResetCommand.builder()
                    .email(email)
                    .password(password)
                    .build();
        }
    }
}
