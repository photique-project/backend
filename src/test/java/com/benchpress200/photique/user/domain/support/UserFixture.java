package com.benchpress200.photique.user.domain.support;

import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import java.time.LocalDateTime;

public class UserFixture {
    private UserFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id = 1L;
        private String email = "test@example.com"; // RFC 2606에 권장된 예시 TLD 사용
        private String password = "test-password";
        private String nickname = "테스트유저";
        private String profileImage = "test.jpg";
        private String introduction = "테스트소개";
        private Provider provider = Provider.LOCAL;
        private Role role = Role.USER;
        private LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 1, 1);
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

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

        public Builder profileImage(String profileImage) {
            this.profileImage = profileImage;
            return this;
        }

        public Builder introduction(String introduction) {
            this.introduction = introduction;
            return this;
        }

        public Builder provider(Provider provider) {
            this.provider = provider;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder deletedAt(LocalDateTime deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public User build() {
            return User.builder()
                    .id(id)
                    .email(email)
                    .password(password)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .introduction(introduction)
                    .provider(provider)
                    .role(role)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .deletedAt(deletedAt)
                    .build();
        }
    }
}
