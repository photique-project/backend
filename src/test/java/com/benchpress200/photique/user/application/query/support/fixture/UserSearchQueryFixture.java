package com.benchpress200.photique.user.application.query.support.fixture;

import com.benchpress200.photique.user.application.query.model.UserSearchQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class UserSearchQueryFixture {
    private UserSearchQueryFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String keyword = "기본 키워드";
        private Pageable pageable = PageRequest.of(0, 30);

        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder pageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        public UserSearchQuery build() {
            return UserSearchQuery.builder()
                    .keyword(keyword)
                    .pageable(pageable)
                    .build();
        }
    }
}
