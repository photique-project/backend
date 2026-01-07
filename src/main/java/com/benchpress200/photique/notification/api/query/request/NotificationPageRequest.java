package com.benchpress200.photique.notification.api.query.request;

import com.benchpress200.photique.notification.application.query.model.NotificationPageQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@AllArgsConstructor
public class NotificationPageRequest {
    private static final String DEFAULT_SORT_BY = "createdAt";

    @PositiveOrZero(message = "Invalid page")
    private Integer page;

    @Min(value = 1, message = "Invalid size")
    @Max(value = 50, message = "Invalid size")
    private Integer size;

    public NotificationPageQuery toQuery() {
        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 30;
        }

        Sort sort = Sort.by(DEFAULT_SORT_BY).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return NotificationPageQuery.builder()
                .pageable(pageable)
                .build();
    }
}
