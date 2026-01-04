package com.benchpress200.photique.exhibition.api.command.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExhibitionWorkCreateRequest {
    private static final String INVALID_WORK_ORDER = "Invalid work order";
    private static final String INVALID_WORK_TITLE = "Invalid work title";
    private static final String INVALID_WORK_DESCRIPTION = "Invalid work description";

    @NotNull(message = INVALID_WORK_ORDER)
    @Min(value = 0, message = INVALID_WORK_ORDER)
    @Max(value = 9, message = INVALID_WORK_ORDER)
    private Integer displayOrder;

    @NotBlank(message = INVALID_WORK_TITLE)
    @Size(min = 1, max = 30, message = INVALID_WORK_TITLE)
    private String title;

    @NotBlank(message = INVALID_WORK_DESCRIPTION)
    @Size(min = 1, max = 200, message = INVALID_WORK_DESCRIPTION)
    private String description;
}
