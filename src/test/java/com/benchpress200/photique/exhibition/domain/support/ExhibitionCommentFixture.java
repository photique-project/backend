package com.benchpress200.photique.exhibition.domain.support;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;

public class ExhibitionCommentFixture {
    private ExhibitionCommentFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User writer = UserFixture.builder().id(1L).build();
        private Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();
        private String content = "기본 감상평";

        public Builder writer(User writer) {
            this.writer = writer;
            return this;
        }

        public Builder exhibition(Exhibition exhibition) {
            this.exhibition = exhibition;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public ExhibitionComment build() {
            return ExhibitionComment.builder()
                    .writer(writer)
                    .exhibition(exhibition)
                    .content(content)
                    .build();
        }
    }
}
