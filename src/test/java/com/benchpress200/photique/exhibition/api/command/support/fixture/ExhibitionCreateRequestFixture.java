package com.benchpress200.photique.exhibition.api.command.support.fixture;

import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCreateRequest;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionWorkCreateRequest;
import java.util.List;

public class ExhibitionCreateRequestFixture {
    private ExhibitionCreateRequestFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title = "기본 전시회 제목";
        private String description = "기본 전시회 설명";
        private String cardColor = "#FFFFFF";
        private List<String> tags = List.of("태그1", "태그2");
        private List<ExhibitionWorkCreateRequest> works = List.of(
                ExhibitionWorkCreateRequestFixture.builder().build()
        );

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder cardColor(String cardColor) {
            this.cardColor = cardColor;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder works(List<ExhibitionWorkCreateRequest> works) {
            this.works = works;
            return this;
        }

        public ExhibitionCreateRequest build() {
            ExhibitionCreateRequest request = new ExhibitionCreateRequest();
            request.setTitle(title);
            request.setDescription(description);
            request.setCardColor(cardColor);
            request.setTags(tags);
            request.setWorks(works);
            return request;
        }
    }
}
