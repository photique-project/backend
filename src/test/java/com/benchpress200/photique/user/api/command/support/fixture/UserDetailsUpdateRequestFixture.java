package com.benchpress200.photique.user.api.command.support.fixture;

public class UserDetailsUpdateRequestFixture {
    private final String nickname;
    private final String introduction;

    private UserDetailsUpdateRequestFixture(Builder builder) {
        this.nickname = builder.nickname;
        this.introduction = builder.introduction;
    }

    public String getNickname() {
        return nickname;
    }

    public String getIntroduction() {
        return introduction;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nickname = "테스트닉";
        private String introduction = "안녕하세요";

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder introduction(String introduction) {
            this.introduction = introduction;
            return this;
        }

        public UserDetailsUpdateRequestFixture build() {
            return new UserDetailsUpdateRequestFixture(this);
        }
    }
}
