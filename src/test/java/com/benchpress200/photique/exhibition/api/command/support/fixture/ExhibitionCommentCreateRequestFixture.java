package com.benchpress200.photique.exhibition.api.command.support.fixture;

import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCommentCreateRequest;
import org.springframework.test.util.ReflectionTestUtils;

public class ExhibitionCommentCreateRequestFixture {
    private ExhibitionCommentCreateRequestFixture() {
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

        public ExhibitionCommentCreateRequest build() {
            ExhibitionCommentCreateRequest request = new ExhibitionCommentCreateRequest();
            ReflectionTestUtils.setField(request, "content", content);
            return request;
        }
    }
}
