package com.benchpress200.photique.user.presentation.query.dto.request;

import com.benchpress200.photique.user.application.query.model.UserSearchQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@AllArgsConstructor
public class UserSearchRequest {
    private static final String SORT_BY_COLUMN = "nickname";

    @NotNull(message = "Invalid keyword")
    @Pattern(regexp = "^[^\\s]{1,11}$", message = "Invalid keyword")
    private String keyword;

    @PositiveOrZero(message = "Invalid page")
    private Integer page;

    @Min(value = 1, message = "Invalid size")
    @Max(value = 50, message = "Invalid size")
    private Integer size;

    public UserSearchQuery toQuery() {
        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 30;
        }

        Sort sort = Sort.by(SORT_BY_COLUMN).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return UserSearchQuery.builder()
                .keyword(keyword)
                .pageable(pageable)
                .build();
    }
}
