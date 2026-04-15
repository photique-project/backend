package com.benchpress200.photique.singlework.application.query.support.fixture;

import com.benchpress200.photique.singlework.application.query.model.SingleWorkCommentsQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class SingleWorkCommentsQueryFixture {
    private SingleWorkCommentsQueryFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long singleWorkId = 1L;
        private Pageable pageable = PageRequest.of(0, 30);

        public Builder singleWorkId(Long singleWorkId) {
            this.singleWorkId = singleWorkId;
            return this;
        }

        public Builder pageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        public SingleWorkCommentsQuery build() {
            return SingleWorkCommentsQuery.builder()
                    .singleWorkId(singleWorkId)
                    .pageable(pageable)
                    .build();
        }
    }
}
