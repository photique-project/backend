package com.benchpress200.photique.singlework.api.validator;

import com.benchpress200.photique.singlework.api.validator.annotation.Enum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

public class EnumValidator implements ConstraintValidator<Enum, Object> {
    private static final String VALID_METHOD_NAME = "isValid";

    private Class<? extends java.lang.Enum<?>> enumClass;

    @Override
    public void initialize(Enum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 미입력 허용
        if (value == null || value.toString().isEmpty()) {
            return true;
        }

        try {
            Method isValidMethod = enumClass.getMethod(VALID_METHOD_NAME, String.class);
            return (boolean) isValidMethod.invoke(null, value.toString());
        } catch (Exception e) {
            return false;
        }
    }
}
