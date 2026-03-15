package com.benchpress200.photique.exhibition.application.query.support.fixture;

import com.benchpress200.photique.exhibition.application.query.result.ExhibitionDetailsResult;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionWorkResult;
import com.benchpress200.photique.exhibition.application.query.result.Writer;
import java.time.LocalDateTime;
import java.util.List;

public class ExhibitionDetailsResultFixture {
    private ExhibitionDetailsResultFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id = 1L;
        private Writer writer = Writer.of(1L, "작성자닉네임", "https://example.com/profile.jpg");
        private String title = "기본 전시회 제목";
        private String description = "기본 전시회 설명";
        private List<String> tags = List.of("태그1", "태그2");
        private List<ExhibitionWorkResult> works = List.of();
        private Long viewCount = 0L;
        private Long likeCount = 0L;
        private LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 0, 0);
        private boolean isFollowing = false;
        private boolean isLiked = false;
        private boolean isBookmarked = false;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder writer(Writer writer) {
            this.writer = writer;
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

        public Builder works(List<ExhibitionWorkResult> works) {
            this.works = works;
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

        public Builder isFollowing(boolean isFollowing) {
            this.isFollowing = isFollowing;
            return this;
        }

        public Builder isLiked(boolean isLiked) {
            this.isLiked = isLiked;
            return this;
        }

        public Builder isBookmarked(boolean isBookmarked) {
            this.isBookmarked = isBookmarked;
            return this;
        }

        public ExhibitionDetailsResult build() {
            return ExhibitionDetailsResult.builder()
                    .id(id)
                    .writer(writer)
                    .title(title)
                    .description(description)
                    .tags(tags)
                    .works(works)
                    .viewCount(viewCount)
                    .likeCount(likeCount)
                    .createdAt(createdAt)
                    .isFollowing(isFollowing)
                    .isLiked(isLiked)
                    .isBookmarked(isBookmarked)
                    .build();
        }
    }
}
