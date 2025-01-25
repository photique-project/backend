package com.benchpress200.photique.common.dtovalidator.validator;

import com.benchpress200.photique.auth.domain.enumeration.AuthType;
import com.benchpress200.photique.common.dtovalidator.annotation.ValidAuthType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AuthTypeValidator implements ConstraintValidator<ValidAuthType, AuthType> {

    @Override
    public boolean isValid(AuthType authType, ConstraintValidatorContext context) {
        if (authType == null) {
            return false;
        }

        return AuthType.isValid(authType.getValue());
    }
}
