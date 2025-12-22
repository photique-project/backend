package com.benchpress200.photique.tag.presentation.validator;

import com.benchpress200.photique.tag.presentation.validator.annotation.Tag;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class TagValidator implements ConstraintValidator<Tag, List<String>> {
    private static final int MAX_TAG_COUNT = 5;
    private static final int MAX_TAG_LENGTH = 10;
    private static final String EMPTY_SPACE = " ";

    @Override
    public boolean isValid(List<String> tags, ConstraintValidatorContext context) {
        if (tags == null) {
            return true;
        }

        if (tags.size() > MAX_TAG_COUNT) {
            return false;
        }

        for (String tag : tags) {
            if (tag.isBlank()) {
                return false;
            }

            if (tag.contains(EMPTY_SPACE)) {
                return false;
            }

            if (tag.length() > MAX_TAG_LENGTH) {
                return false;
            }
        }

        return true;
    }
}
