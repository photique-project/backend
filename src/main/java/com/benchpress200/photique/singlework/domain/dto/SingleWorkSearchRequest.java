package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.singlework.presentation.validator.annotation.Enum;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
public class SingleWorkSearchRequest {
    @Enum(enumClass = Target.class, message = "Invalid value of target")
    private String target;

    private List<String> keywords;

    private List<String> categories;

    @Getter
    private Long userId;

    public List<Category> getCategories() {
        if (categories == null) {
            return new ArrayList<>();
        }

        return categories.stream()
                .map(Category::from)
                .toList();
    }

    public Target getTarget() {
        return Target.from(target);
    }

    public List<String> getKeywords() {
        if (keywords == null) {
            return new ArrayList<>();
        }

        return keywords;
    }
}
