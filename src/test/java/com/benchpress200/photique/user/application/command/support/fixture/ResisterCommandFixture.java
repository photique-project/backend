package com.benchpress200.photique.user.application.command.support.fixture;

import com.benchpress200.photique.user.application.command.model.ResisterCommand;
import org.springframework.web.multipart.MultipartFile;

public class ResisterCommandFixture {
    private ResisterCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email = "test@example.com";
        private String password = "기본비밀번호1!";
        private String nickname = "기본닉네임";
        private MultipartFile profileImage = null;

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

        public Builder profileImage(MultipartFile profileImage) {
            this.profileImage = profileImage;
            return this;
        }

        public ResisterCommand build() {
            return ResisterCommand.builder()
                    .email(email)
                    .password(password)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .build();
        }
    }
}
