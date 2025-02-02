package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.common.dtovalidator.annotation.Enum;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Sort;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleWorkSearchRequest {
    @Size(max = 50, message = "The input must not exceed 50 characters")
    private String q;

    @Enum(enumClass = Target.class, message = "Invalid value of target")
    private String target;

    @Enum(enumClass = Sort.class, message = "Invalid value of sort")
    private String sort;

    @Enum(enumClass = Category.class, message = "Invalid value of category")
    private String category;

    @Min(value = 1, message = "Page must be at least 1")
    @Max(value = 1, message = "Page must not exceed 1")
    private Long page;

    @Min(value = 30, message = "Size must be at least 30")
    @Max(value = 30, message = "Size must not exceed 30")
    private Long size;
}
