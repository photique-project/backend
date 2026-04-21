package com.benchpress200.photique.exhibition.domain.support;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;

public class ExhibitionFixture {
    private ExhibitionFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long writerId = 1L;
        private User writer = null;
        private String title = "기본 전시회 제목";
        private String description = "기본 전시회 설명";
        private String cardColor = "#FFFFFF";

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder writerId(Long writerId) {
            this.writerId = writerId;
            return this;
        }

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

        public Builder cardColor(String cardColor) {
            this.cardColor = cardColor;
            return this;
        }

        public Exhibition build() {
            User resolvedWriter = (writer != null) ? writer : UserFixture.builder().id(writerId).build();

            return Exhibition.builder()
                    .id(id)
                    .writer(resolvedWriter)
                    .title(title)
                    .description(description)
                    .cardColor(cardColor)
                    .viewCount(0L)
                    .likeCount(0L)
                    .build();
        }
    }
}
