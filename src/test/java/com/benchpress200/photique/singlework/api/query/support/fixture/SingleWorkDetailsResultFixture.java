package com.benchpress200.photique.singlework.api.query.support.fixture;

import com.benchpress200.photique.singlework.application.query.result.SingleWorkDetailsResult;
import com.benchpress200.photique.singlework.application.query.result.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SingleWorkDetailsResultFixture {

    private SingleWorkDetailsResultFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id = 1L;
        private Writer writer = Writer.of(1L, "작성자닉네임", "https://example.com/profile.jpg");
        private String title = "기본 제목";
        private String description = "기본 설명";
        private String image = "https://example.com/image.jpg";
        private String camera = "기본 카메라";
        private String lens = "기본 렌즈";
        private String aperture = "f/1.8";
        private String shutterSpeed = "1/100";
        private String iso = "ISO 100";
        private String category = "풍경";
        private String location = "서울";
        private LocalDate date = LocalDate.of(2024, 1, 1);
        private List<String> tags = List.of("태그1", "태그2");
        private Long likeCount = 10L;
        private Long viewCount = 100L;
        private LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 0, 0);
        private boolean isLiked = false;
        private boolean isFollowing = false;

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

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder camera(String camera) {
            this.camera = camera;
            return this;
        }

        public Builder lens(String lens) {
            this.lens = lens;
            return this;
        }

        public Builder aperture(String aperture) {
            this.aperture = aperture;
            return this;
        }

        public Builder shutterSpeed(String shutterSpeed) {
            this.shutterSpeed = shutterSpeed;
            return this;
        }

        public Builder iso(String iso) {
            this.iso = iso;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder likeCount(Long likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public Builder viewCount(Long viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder isLiked(boolean isLiked) {
            this.isLiked = isLiked;
            return this;
        }

        public Builder isFollowing(boolean isFollowing) {
            this.isFollowing = isFollowing;
            return this;
        }

        public SingleWorkDetailsResult build() {
            return SingleWorkDetailsResult.builder()
                    .id(id)
                    .writer(writer)
                    .title(title)
                    .description(description)
                    .image(image)
                    .camera(camera)
                    .lens(lens)
                    .aperture(aperture)
                    .shutterSpeed(shutterSpeed)
                    .iso(iso)
                    .category(category)
                    .location(location)
                    .date(date)
                    .tags(tags)
                    .likeCount(likeCount)
                    .viewCount(viewCount)
                    .createdAt(createdAt)
                    .isLiked(isLiked)
                    .isFollowing(isFollowing)
                    .build();
        }
    }
}
