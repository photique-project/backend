package com.benchpress200.photique.user.api.command.support.fixture;

public class ResisterRequestFixture {
    private final String email;
    private final String password;
    private final String nickname;

    private ResisterRequestFixture(Builder builder) {
        this.email = builder.email;
        this.password = builder.password;
        this.nickname = builder.nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email = "test@example.com";
        private String password = "Password1!";
        private String nickname = "테스트닉";

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public ResisterRequestFixture build() {
            return new ResisterRequestFixture(this);
        }
    }
}
