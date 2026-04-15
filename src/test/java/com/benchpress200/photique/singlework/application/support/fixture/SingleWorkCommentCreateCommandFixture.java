package com.benchpress200.photique.singlework.application.support.fixture;

import com.benchpress200.photique.singlework.application.command.model.SingleWorkCommentCreateCommand;

public class SingleWorkCommentCreateCommandFixture {
    private SingleWorkCommentCreateCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long singleWorkId = 1L;
        private String content = "기본 댓글 내용";

        public Builder singleWorkId(Long singleWorkId) {
            this.singleWorkId = singleWorkId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public SingleWorkCommentCreateCommand build() {
            return SingleWorkCommentCreateCommand.builder()
                    .singleWorkId(singleWorkId)
                    .content(content)
                    .build();
        }
    }
}
