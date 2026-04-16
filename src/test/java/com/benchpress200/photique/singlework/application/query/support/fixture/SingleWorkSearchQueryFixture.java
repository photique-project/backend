package com.benchpress200.photique.singlework.application.query.support.fixture;

import com.benchpress200.photique.singlework.application.query.model.SingleWorkSearchQuery;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class SingleWorkSearchQueryFixture {
    private SingleWorkSearchQueryFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Target target = Target.WORK;
        private String keyword = "기본 키워드";
        private List<Category> categories = List.of(Category.LANDSCAPE);
        private Pageable pageable = PageRequest.of(0, 30);

        public Builder target(Target target) {
            this.target = target;
            return this;
        }

        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder categories(List<Category> categories) {
            this.categories = categories;
            return this;
        }

        public Builder pageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        public SingleWorkSearchQuery build() {
            return SingleWorkSearchQuery.builder()
                    .target(target)
                    .keyword(keyword)
                    .categories(categories)
                    .pageable(pageable)
                    .build();
        }
    }
}
