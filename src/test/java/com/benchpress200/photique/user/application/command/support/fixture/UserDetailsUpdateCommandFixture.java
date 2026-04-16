package com.benchpress200.photique.user.application.command.support.fixture;

import com.benchpress200.photique.user.application.command.model.UserDetailsUpdateCommand;
import org.springframework.web.multipart.MultipartFile;

public class UserDetailsUpdateCommandFixture {
    private UserDetailsUpdateCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userId = 1L;
        private String nickname = "기본닉네임";
        private String introduction = "기본소개";
        private MultipartFile profileImage = null;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder introduction(String introduction) {
            this.introduction = introduction;
            return this;
        }

        public Builder profileImage(MultipartFile profileImage) {
            this.profileImage = profileImage;
            return this;
        }

        public UserDetailsUpdateCommand build() {
            return UserDetailsUpdateCommand.builder()
                    .userId(userId)
                    .nickname(nickname)
                    .introduction(introduction)
                    .profileImage(profileImage)
                    .build();
        }
    }
}
