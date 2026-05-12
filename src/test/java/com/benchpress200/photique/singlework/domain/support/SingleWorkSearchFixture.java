package com.benchpress200.photique.singlework.domain.support;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import java.util.List;

public class SingleWorkSearchFixture {

    private SingleWorkSearchFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SingleWork singleWork;
        private List<String> tags = List.of();

        public Builder singleWork(SingleWork singleWork) {
            this.singleWork = singleWork;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public SingleWorkSearch build() {
            return SingleWorkSearch.of(singleWork, tags);
        }
    }
}
