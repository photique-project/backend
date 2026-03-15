package com.benchpress200.photique.singlework.application.query.support.fixture;

import com.benchpress200.photique.exhibition.application.query.result.SearchedExhibition;
import com.benchpress200.photique.singlework.application.query.result.MyExhibitionSearchResult;
import java.util.List;

public class MyExhibitionSearchResultFixture {
    private MyExhibitionSearchResultFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int page = 0;
        private int size = 30;
        private long totalElements = 0L;
        private int totalPages = 0;
        private boolean isFirst = true;
        private boolean isLast = true;
        private boolean hasNext = false;
        private boolean hasPrevious = false;
        private List<SearchedExhibition> exhibitions = List.of();

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

        public Builder isFirst(boolean isFirst) {
            this.isFirst = isFirst;
            return this;
        }

        public Builder isLast(boolean isLast) {
            this.isLast = isLast;
            return this;
        }

        public Builder hasNext(boolean hasNext) {
            this.hasNext = hasNext;
            return this;
        }

        public Builder hasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
            return this;
        }

        public MyExhibitionSearchResult build() {
            return MyExhibitionSearchResult.builder()
                    .page(page)
                    .size(size)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .isFirst(isFirst)
                    .isLast(isLast)
                    .hasNext(hasNext)
                    .hasPrevious(hasPrevious)
                    .exhibitions(exhibitions)
                    .build();
        }
    }
}
