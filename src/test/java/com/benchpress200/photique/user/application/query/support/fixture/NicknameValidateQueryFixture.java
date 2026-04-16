package com.benchpress200.photique.user.application.query.support.fixture;

import com.benchpress200.photique.user.application.query.model.NicknameValidateQuery;

public class NicknameValidateQueryFixture {
    private NicknameValidateQueryFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nickname = "기본닉네임";

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public NicknameValidateQuery build() {
            return NicknameValidateQuery.builder()
                    .nickname(nickname)
                    .build();
        }
    }
}
