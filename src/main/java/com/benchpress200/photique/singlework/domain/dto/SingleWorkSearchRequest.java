package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.singlework.validation.annotation.Enum;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;

@Setter
public class SingleWorkSearchRequest {
    @Enum(enumClass = Target.class, message = "Invalid value of target")
    private String target;

    private List<String> keywords;

    private List<String> categories;

    public List<Category> getCategories() {
        if (categories == null) {
            return new ArrayList<>();
        }

        return categories.stream()
                .map(Category::fromValue)
                .toList();
    }

    public Target getTarget() {
        return Target.fromValue(target);
    }

    public List<String> getKeywords() {
        if (keywords == null) {
            return new ArrayList<>();
        }

        return keywords;
    }
}
