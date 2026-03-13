package com.benchpress200.photique.exhibition.api.command.support.fixture;

import com.benchpress200.photique.exhibition.api.command.request.ExhibitionWorkCreateRequest;

public class ExhibitionWorkCreateRequestFixture {
    private ExhibitionWorkCreateRequestFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer displayOrder = 0;
        private String title = "기본 작품 제목";
        private String description = "기본 작품 설명";

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

        public ExhibitionWorkCreateRequest build() {
            ExhibitionWorkCreateRequest request = new ExhibitionWorkCreateRequest();
            request.setDisplayOrder(displayOrder);
            request.setTitle(title);
            request.setDescription(description);
            return request;
        }
    }
}
