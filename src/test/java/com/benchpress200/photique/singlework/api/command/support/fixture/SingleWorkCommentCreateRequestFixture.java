package com.benchpress200.photique.singlework.api.command.support.fixture;

import com.benchpress200.photique.singlework.api.command.request.SingleWorkCommentCreateRequest;
import org.springframework.test.util.ReflectionTestUtils;

public class SingleWorkCommentCreateRequestFixture {
    private SingleWorkCommentCreateRequestFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String content = "기본 댓글 내용";

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public SingleWorkCommentCreateRequest build() {
            SingleWorkCommentCreateRequest request = new SingleWorkCommentCreateRequest();
            ReflectionTestUtils.setField(request, "content", content);
            return request;
        }
    }
}
