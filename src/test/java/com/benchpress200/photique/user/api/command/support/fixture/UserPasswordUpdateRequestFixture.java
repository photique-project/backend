package com.benchpress200.photique.user.api.command.support.fixture;

public class UserPasswordUpdateRequestFixture {
    private final String password;

    private UserPasswordUpdateRequestFixture(Builder builder) {
        this.password = builder.password;
    }

    public String getPassword() {
        return password;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String password = "Password1!";

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public UserPasswordUpdateRequestFixture build() {
            return new UserPasswordUpdateRequestFixture(this);
        }
    }
}
