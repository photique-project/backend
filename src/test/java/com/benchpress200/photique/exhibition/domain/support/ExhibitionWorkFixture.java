package com.benchpress200.photique.exhibition.domain.support;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;

public class ExhibitionWorkFixture {
    private ExhibitionWorkFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();
        private Integer displayOrder = 0;
        private String title = "기본 작품 제목";
        private String description = "기본 작품 설명";
        private String image = "https://test-bucket/exhibition/work.jpg";

        public Builder exhibition(Exhibition exhibition) {
            this.exhibition = exhibition;
            return this;
        }

        public Builder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
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

        public ExhibitionWork build() {
            return ExhibitionWork.builder()
                    .exhibition(exhibition)
                    .displayOrder(displayOrder)
                    .title(title)
                    .description(description)
                    .image(image)
                    .build();
        }
    }
}
