package com.benchpress200.photique.singlework.api.command.support.fixture;

import com.benchpress200.photique.singlework.api.command.request.SingleWorkCommentUpdateRequest;
import org.springframework.test.util.ReflectionTestUtils;

public class SingleWorkCommentUpdateRequestFixture {
    private SingleWorkCommentUpdateRequestFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String content = "수정된 댓글 내용";

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public SingleWorkCommentUpdateRequest build() {
            SingleWorkCommentUpdateRequest request = new SingleWorkCommentUpdateRequest();
            ReflectionTestUtils.setField(request, "content", content);
            return request;
        }
    }
}
