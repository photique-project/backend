package com.benchpress200.photique.exhibition.api.command.support.fixture;

import com.benchpress200.photique.exhibition.api.command.request.ExhibitionWorkUpdateRequest;

public class ExhibitionWorkUpdateRequestFixture {
    private ExhibitionWorkUpdateRequestFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id = 1L;
        private Integer displayOrder = 0;
        private String title = "수정된 작품 제목";
        private String description = "수정된 작품 설명";

        public Builder id(Long id) {
            this.id = id;
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

        public ExhibitionWorkUpdateRequest build() {
            ExhibitionWorkUpdateRequest request = new ExhibitionWorkUpdateRequest();
            request.setId(id);
            request.setDisplayOrder(displayOrder);
            request.setTitle(title);
            request.setDescription(description);
            return request;
        }
    }
}
