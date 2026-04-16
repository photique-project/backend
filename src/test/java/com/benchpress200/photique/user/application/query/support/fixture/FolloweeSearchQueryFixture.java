package com.benchpress200.photique.user.application.query.support.fixture;

import com.benchpress200.photique.user.application.query.model.FolloweeSearchQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class FolloweeSearchQueryFixture {
    private FolloweeSearchQueryFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userId = 1L;
        private String keyword = "기본 키워드";
        private Pageable pageable = PageRequest.of(0, 30);

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder pageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        public FolloweeSearchQuery build() {
            return FolloweeSearchQuery.builder()
                    .userId(userId)
                    .keyword(keyword)
                    .pageable(pageable)
                    .build();
        }
    }
}
