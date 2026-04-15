package com.benchpress200.photique.singlework.domain.support;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;

public class SingleWorkCommentFixture {
    private SingleWorkCommentFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User writer = UserFixture.builder().id(1L).build();
        private SingleWork singleWork = SingleWorkFixture.builder().build();
        private String content = "기본 댓글 내용";

        public Builder writer(User writer) {
            this.writer = writer;
            return this;
        }

        public Builder singleWork(SingleWork singleWork) {
            this.singleWork = singleWork;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public SingleWorkComment build() {
            return SingleWorkComment.builder()
                    .writer(writer)
                    .singleWork(singleWork)
                    .content(content)
                    .build();
        }
    }
}
