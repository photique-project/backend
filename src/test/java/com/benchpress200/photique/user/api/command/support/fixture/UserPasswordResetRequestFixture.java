package com.benchpress200.photique.user.api.command.support.fixture;

public class UserPasswordResetRequestFixture {
    private final String email;
    private final String password;

    private UserPasswordResetRequestFixture(Builder builder) {
        this.email = builder.email;
        this.password = builder.password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email = "test@example.com";
        private String password = "Password1!";

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public UserPasswordResetRequestFixture build() {
            return new UserPasswordResetRequestFixture(this);
        }
    }
}
