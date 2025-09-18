package com.benchpress200.photique.user.presentation.request;

import com.benchpress200.photique.user.application.query.SearchUsersQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class SearchUsersRequest {
    @NotNull(message = "Invalid keyword")
    @Pattern(regexp = "^[^\\s]{1,11}$", message = "Invalid keyword")
    private String keyword;

    @PositiveOrZero(message = "Invalid page")
    private Integer page;

    @Min(value = 1, message = "Invalid size")
    @Max(value = 50, message = "Invalid size")
    private Integer size;

    public SearchUsersQuery toQuery() {
        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 30;
        }

        Sort sort = Sort.by("nickname").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return SearchUsersQuery.builder()
                .keyword(keyword)
                .pageable(pageable)
                .build();
    }
}
