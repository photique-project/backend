package com.benchpress200.photique.exhibition.application.support.fixture;

import com.benchpress200.photique.exhibition.application.command.model.ExhibitionUpdateCommand;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionWorkUpdateCommand;
import java.util.List;

public class ExhibitionUpdateCommandFixture {
    private ExhibitionUpdateCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long exhibitionId = 1L;
        private boolean updateTitle = true;
        private String title = "수정된 제목";
        private boolean updateDescription = true;
        private String description = "수정된 설명";
        private boolean updateCardColor = true;
        private String cardColor = "#000000";
        private boolean updateTags = true;
        private List<String> tags = List.of("수정태그1");
        private boolean updateWorks = true;
        private List<ExhibitionWorkUpdateCommand> works = List.of(
                ExhibitionWorkUpdateCommandFixture.builder().build()
        );
        private boolean update = true;

        public Builder exhibitionId(Long exhibitionId) {
            this.exhibitionId = exhibitionId;
            return this;
        }

        public Builder updateTitle(boolean updateTitle) {
            this.updateTitle = updateTitle;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder updateDescription(boolean updateDescription) {
            this.updateDescription = updateDescription;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder updateCardColor(boolean updateCardColor) {
            this.updateCardColor = updateCardColor;
            return this;
        }

        public Builder cardColor(String cardColor) {
            this.cardColor = cardColor;
            return this;
        }

        public Builder updateTags(boolean updateTags) {
            this.updateTags = updateTags;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder updateWorks(boolean updateWorks) {
            this.updateWorks = updateWorks;
            return this;
        }

        public Builder works(List<ExhibitionWorkUpdateCommand> works) {
            this.works = works;
            return this;
        }

        public Builder update(boolean update) {
            this.update = update;
            return this;
        }

        public ExhibitionUpdateCommand build() {
            return ExhibitionUpdateCommand.builder()
                    .exhibitionId(exhibitionId)
                    .updateTitle(updateTitle)
                    .title(title)
                    .updateDescription(updateDescription)
                    .description(description)
                    .updateCardColor(updateCardColor)
                    .cardColor(cardColor)
                    .updateTags(updateTags)
                    .tags(tags)
                    .updateWorks(updateWorks)
                    .works(works)
                    .update(update)
                    .build();
        }
    }
}
