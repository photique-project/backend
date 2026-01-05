package com.benchpress200.photique.exhibition.api.query.request;

import com.benchpress200.photique.common.api.validator.annotation.Enum;
import com.benchpress200.photique.exhibition.api.validator.SortValidator;
import com.benchpress200.photique.exhibition.application.query.model.ExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.domain.enumeration.Target;
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
public class ExhibitionSearchRequest {
    private static final String INVALID_TARGET = "Invalid target";
    private static final String INVALID_KEYWORD = "Invalid keyword";
    private static final String INVALID_CATEGORY = "Invalid category";
    private static final String INVALID_PAGE = "Invalid page";
    private static final String INVALID_SIZE = "Invalid size";

    @Enum(enumClass = Target.class, message = INVALID_TARGET)
    private String target;

    @Size(min = 2, max = 100, message = INVALID_KEYWORD)
    private String keyword;

    @PositiveOrZero(message = INVALID_PAGE)
    private Integer page;

    @Min(value = 1, message = INVALID_SIZE)
    @Max(value = 50, message = INVALID_SIZE)
    private Integer size;

    private String sort;

    public ExhibitionSearchQuery toQuery() {
        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 30;
        }

        Sort sort = SortValidator.verifyAndParseExhibitionSearch(this.sort);
        Pageable pageable = PageRequest.of(page, size, sort);

        return ExhibitionSearchQuery.builder()
                .target(Target.from(target))
                .keyword(keyword)
                .pageable(pageable)
                .build();
    }
}
