package com.benchpress200.photique.singlework.application.query.support.fixture;

import com.benchpress200.photique.singlework.application.query.model.MySingleWorkSearchQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class MySingleWorkSearchQueryFixture {
    private MySingleWorkSearchQueryFixture() {
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

        public MySingleWorkSearchQuery build() {
            return MySingleWorkSearchQuery.builder()
                    .keyword(keyword)
                    .pageable(pageable)
                    .build();
        }
    }
}
