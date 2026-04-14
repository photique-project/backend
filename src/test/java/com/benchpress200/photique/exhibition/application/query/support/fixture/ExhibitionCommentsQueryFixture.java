package com.benchpress200.photique.exhibition.application.query.support.fixture;

import com.benchpress200.photique.exhibition.application.query.model.ExhibitionCommentsQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class ExhibitionCommentsQueryFixture {
    private ExhibitionCommentsQueryFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long exhibitionId = 1L;
        private Pageable pageable = PageRequest.of(0, 5);

        public Builder exhibitionId(Long exhibitionId) {
            this.exhibitionId = exhibitionId;
            return this;
        }

        public Builder pageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        public ExhibitionCommentsQuery build() {
            return ExhibitionCommentsQuery.builder()
                    .exhibitionId(exhibitionId)
                    .pageable(pageable)
                    .build();
        }
    }
}
