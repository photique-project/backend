package com.benchpress200.photique.exhibition.api.command.support.fixture;

import com.benchpress200.photique.exhibition.api.command.request.ExhibitionWorkUpdateRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExhibitionUpdateRequestFixture {
    private ExhibitionUpdateRequestFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean updateTitle = true;
        private String title = "수정된 전시회 제목";
        private boolean updateDescription = false;
        private String description = null;
        private boolean updateCardColor = false;
        private String cardColor = null;
        private boolean updateTags = false;
        private List<String> tags = null;
        private boolean updateWorks = false;
        private List<ExhibitionWorkUpdateRequest> works = null;

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

        public Builder works(List<ExhibitionWorkUpdateRequest> works) {
            this.works = works;
            return this;
        }

        public Map<String, Object> build() {
            Map<String, Object> map = new HashMap<>();
            map.put("updateTitle", updateTitle);
            map.put("title", title);
            map.put("updateDescription", updateDescription);
            map.put("description", description);
            map.put("updateCardColor", updateCardColor);
            map.put("cardColor", cardColor);
            map.put("updateTags", updateTags);
            map.put("tags", tags);
            map.put("updateWorks", updateWorks);
            map.put("works", works);
            return map;
        }
    }
}
