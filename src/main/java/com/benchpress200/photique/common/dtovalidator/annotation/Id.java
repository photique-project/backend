package com.benchpress200.photique.common.dtovalidator.annotation;

import com.benchpress200.photique.common.dtovalidator.validator.IdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IdValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
    String message() default "Invalid id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
