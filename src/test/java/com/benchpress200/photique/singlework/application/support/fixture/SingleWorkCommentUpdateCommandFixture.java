package com.benchpress200.photique.singlework.application.support.fixture;

import com.benchpress200.photique.singlework.application.command.model.SingleWorkCommentUpdateCommand;

public class SingleWorkCommentUpdateCommandFixture {
    private SingleWorkCommentUpdateCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long commentId = 1L;
        private String content = "수정된 댓글 내용";

        public Builder commentId(Long commentId) {
            this.commentId = commentId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public SingleWorkCommentUpdateCommand build() {
            return SingleWorkCommentUpdateCommand.builder()
                    .commentId(commentId)
                    .content(content)
                    .build();
        }
    }
}
