package com.benchpress200.photique.common.dtovalidator.validator;

import com.benchpress200.photique.common.dtovalidator.annotation.Id;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IdValidator implements ConstraintValidator<Id, Long> {
    @Override
    public void initialize(Id constraintAnnotation) {
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        return id > 0;
    }
}
