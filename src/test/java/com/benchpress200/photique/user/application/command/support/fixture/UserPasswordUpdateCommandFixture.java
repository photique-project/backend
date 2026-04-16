package com.benchpress200.photique.user.application.command.support.fixture;

import com.benchpress200.photique.user.application.command.model.UserPasswordUpdateCommand;

public class UserPasswordUpdateCommandFixture {
    private UserPasswordUpdateCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userId = 1L;
        private String password = "기본비밀번호1!";

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public UserPasswordUpdateCommand build() {
            return UserPasswordUpdateCommand.builder()
                    .userId(userId)
                    .password(password)
                    .build();
        }
    }
}
