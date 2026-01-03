package com.benchpress200.photique.singlework.api.query.request;

import com.benchpress200.photique.singlework.api.validator.SortValidator;
import com.benchpress200.photique.singlework.application.query.model.LikedSingleWorkSearchQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class LikedSingleWorkSearchRequest {
    private static final String INVALID_KEYWORD = "Invalid keyword";
    private static final String INVALID_PAGE = "Invalid page";
    private static final String INVALID_SIZE = "Invalid size";

    @Size(min = 2, max = 100, message = INVALID_KEYWORD)
    private String keyword;

    @PositiveOrZero(message = INVALID_PAGE)
    private Integer page;

    @Min(value = 1, message = INVALID_SIZE)
    @Max(value = 50, message = INVALID_SIZE)
    private Integer size;

    private String sort;

    public LikedSingleWorkSearchQuery toQuery() {
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }

        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 30;
        }

        Sort sort = SortValidator.verifyAndParseMySingleWorkSearch(this.sort);
        Pageable pageable = PageRequest.of(page, size, sort);

        return LikedSingleWorkSearchQuery.builder()
                .keyword(keyword)
                .pageable(pageable)
                .build();
    }
}
