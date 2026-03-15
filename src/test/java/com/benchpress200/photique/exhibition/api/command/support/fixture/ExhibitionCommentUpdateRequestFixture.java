package com.benchpress200.photique.exhibition.api.command.support.fixture;

import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCommentUpdateRequest;
import org.springframework.test.util.ReflectionTestUtils;

public class ExhibitionCommentUpdateRequestFixture {
    private ExhibitionCommentUpdateRequestFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String content = "기본 감상평";

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public ExhibitionCommentUpdateRequest build() {
            ExhibitionCommentUpdateRequest request = new ExhibitionCommentUpdateRequest();
            ReflectionTestUtils.setField(request, "content", content);
            return request;
        }
    }
}
