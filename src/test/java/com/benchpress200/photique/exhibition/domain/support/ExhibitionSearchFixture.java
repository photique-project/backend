package com.benchpress200.photique.exhibition.domain.support;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.List;

public class ExhibitionSearchFixture {
    private ExhibitionSearchFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id = 1L;
        private Long writerId = 1L;
        private String writerNickname = "테스트유저";
        private String writerProfileImage = "http://example.com/profile.jpg";
        private String cardColor = "#FFFFFF";
        private String title = "기본 전시회 제목";
        private String description = "기본 전시회 설명";
        private List<String> tags = List.of();
        private Long viewCount = 0L;
        private Long likeCount = 0L;
        private LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        private LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 1, 0, 0, 0);

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder writerId(Long writerId) {
            this.writerId = writerId;
            return this;
        }

        public Builder writerNickname(String writerNickname) {
            this.writerNickname = writerNickname;
            return this;
        }

        public Builder writerProfileImage(String writerProfileImage) {
            this.writerProfileImage = writerProfileImage;
            return this;
        }

        public Builder cardColor(String cardColor) {
            this.cardColor = cardColor;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder viewCount(Long viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public Builder likeCount(Long likeCount) {
            this.likeCount = likeCount;
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


        public ExhibitionSearch build() {
            User user = User.builder()
                    .id(writerId)
                    .nickname(writerNickname)
                    .profileImage(writerProfileImage)
                    .build();
            
            return ExhibitionSearch.of(
                    id,
                    user,
                    cardColor,
                    title,
                    description,
                    tags,
                    viewCount,
                    likeCount,
                    createdAt,
                    updatedAt
            );
        }
    }
}
