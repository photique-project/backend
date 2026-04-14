package com.benchpress200.photique.exhibition.application.support.fixture;

import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentUpdateCommand;

public class ExhibitionCommentUpdateCommandFixture {
    private ExhibitionCommentUpdateCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long commentId = 1L;
        private String content = "수정된 감상평";

        public Builder commentId(Long commentId) {
            this.commentId = commentId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public ExhibitionCommentUpdateCommand build() {
            return ExhibitionCommentUpdateCommand.builder()
                    .commentId(commentId)
                    .content(content)
                    .build();
        }
    }
}
