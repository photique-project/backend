package com.benchpress200.photique.exhibition.application.query.support.fixture;

import com.benchpress200.photique.exhibition.application.query.model.ExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.domain.enumeration.Target;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class ExhibitionSearchQueryFixture {
    private ExhibitionSearchQueryFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Target target = Target.WORK;
        private String keyword = "기본 키워드";
        private Pageable pageable = PageRequest.of(0, 30);

        public Builder target(Target target) {
            this.target = target;
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

        public ExhibitionSearchQuery build() {
            return ExhibitionSearchQuery.builder()
                    .target(target)
                    .keyword(keyword)
                    .pageable(pageable)
                    .build();
        }
    }
}
