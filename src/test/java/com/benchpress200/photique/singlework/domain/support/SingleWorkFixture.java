package com.benchpress200.photique.singlework.domain.support;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.time.LocalDate;

public class SingleWorkFixture {
    private SingleWorkFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User writer = UserFixture.builder().id(1L).build();
        private String title = "기본 단일작품 제목";
        private String description = "기본 단일작품 설명";
        private String image = "https://example.com/image.jpg";
        private String camera = "기본 카메라";
        private Category category = Category.LANDSCAPE;
        private LocalDate date = LocalDate.now();

        public Builder writer(User writer) {
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

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public SingleWork build() {
            return SingleWork.builder()
                    .writer(writer)
                    .title(title)
                    .description(description)
                    .image(image)
                    .camera(camera)
                    .category(category)
                    .date(date)
                    .build();
        }
    }
}
