package com.benchpress200.photique.exhibition.application.support.fixture;

import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCreateCommand;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionWorkCreateCommand;
import java.util.List;

public class ExhibitionCreateCommandFixture {
    private ExhibitionCreateCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title = "기본 전시회 제목";
        private String description = "기본 전시회 설명";
        private String cardColor = "#FFFFFF";
        private List<String> tags = List.of("태그1");
        private List<ExhibitionWorkCreateCommand> works = List.of(
                ExhibitionWorkCreateCommandFixture.builder().build()
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

        public Builder works(List<ExhibitionWorkCreateCommand> works) {
            this.works = works;
            return this;
        }

        public ExhibitionCreateCommand build() {
            return ExhibitionCreateCommand.builder()
                    .title(title)
                    .description(description)
                    .cardColor(cardColor)
                    .tags(tags)
                    .works(works)
                    .build();
        }
    }
}
