package com.benchpress200.photique.singlework.application.query.support.fixture;

import com.benchpress200.photique.singlework.application.query.result.MySingleWorkSearchResult;
import java.util.List;

public class MySingleWorkSearchResultFixture {

    private MySingleWorkSearchResultFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int page = 0;
        private int size = 10;
        private long totalElements = 0;
        private int totalPages = 0;
        private boolean isFirst = true;
        private boolean isLast = true;
        private boolean hasNext = false;
        private boolean hasPrevious = false;

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public Builder totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public MySingleWorkSearchResult build() {
            return MySingleWorkSearchResult.builder()
                    .page(page)
                    .size(size)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .isFirst(isFirst)
                    .isLast(isLast)
                    .hasNext(hasNext)
                    .hasPrevious(hasPrevious)
                    .singleWorks(List.of())
                    .build();
        }
    }
}
