package com.benchpress200.photique.common.dtovalidator.annotation;

import com.benchpress200.photique.common.dtovalidator.validator.AuthTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = AuthTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAuthType {
    String message() default "Invalid AuthType value. Allowed values are: [join, reset]";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
