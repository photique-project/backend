package com.benchpress200.photique.exhibition.application.support.fixture;

import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentCreateCommand;

public class ExhibitionCommentCreateCommandFixture {
    private ExhibitionCommentCreateCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long exhibitionId = 1L;
        private String content = "기본 감상평";

        public Builder exhibitionId(Long exhibitionId) {
            this.exhibitionId = exhibitionId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public ExhibitionCommentCreateCommand build() {
            return ExhibitionCommentCreateCommand.builder()
                    .exhibitionId(exhibitionId)
                    .content(content)
                    .build();
        }
    }
}
