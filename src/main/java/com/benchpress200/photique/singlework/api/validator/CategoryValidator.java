package com.benchpress200.photique.singlework.api.validator;

import com.benchpress200.photique.singlework.domain.enumeration.Category;
import java.util.List;

public class CategoryValidator {
    private CategoryValidator() {
    }

    public static boolean isValid(String category) {
        return Category.isValid(category);
    }

    public static boolean isValid(List<String> categories) {
        for (String category : categories) {
            if (!Category.isValid(category)) {
                return false;
            }
        }

        return true;
    }
}
