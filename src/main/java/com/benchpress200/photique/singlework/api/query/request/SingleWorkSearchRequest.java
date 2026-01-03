package com.benchpress200.photique.singlework.api.query.request;

import com.benchpress200.photique.singlework.api.query.exception.InvalidFieldToSearch;
import com.benchpress200.photique.singlework.api.validator.CategoryValidator;
import com.benchpress200.photique.singlework.api.validator.SortValidator;
import com.benchpress200.photique.singlework.api.validator.annotation.Enum;
import com.benchpress200.photique.singlework.application.query.model.SingleWorkSearchQuery;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class SingleWorkSearchRequest {
    private static final String INVALID_TARGET = "Invalid target";
    private static final String INVALID_KEYWORD = "Invalid keyword";
    private static final String INVALID_CATEGORY = "Invalid category";
    private static final String INVALID_PAGE = "Invalid page";
    private static final String INVALID_SIZE = "Invalid size";

    @Enum(enumClass = Target.class, message = INVALID_TARGET)
    private String target;

    @Size(min = 2, max = 100, message = INVALID_KEYWORD)
    private String keyword;

    private List<String> categories;

    @PositiveOrZero(message = INVALID_PAGE)
    private Integer page;

    @Min(value = 1, message = INVALID_SIZE)
    @Max(value = 50, message = INVALID_SIZE)
    private Integer size;

    private String sort;

    public SingleWorkSearchQuery toQuery() {
        if (categories == null) {
            categories = new ArrayList<>();
        }

        if (!CategoryValidator.isValid(categories)) {
            throw new InvalidFieldToSearch(INVALID_CATEGORY);
        }

        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 30;
        }

        Sort sort = SortValidator.verifyAndParseSingleWorkSearch(this.sort);
        Pageable pageable = PageRequest.of(page, size, sort);

        // target null 넣었을 때 WORK로 박히는지 확인
        return SingleWorkSearchQuery.builder()
                .target(Target.from(target))
                .keyword(keyword)
                .categories(
                        categories.stream()
                                .map(Category::from)
                                .toList()
                )
                .pageable(pageable)
                .build();
    }
}
