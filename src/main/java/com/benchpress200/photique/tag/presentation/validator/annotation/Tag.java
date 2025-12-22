package com.benchpress200.photique.tag.presentation.validator.annotation;

import com.benchpress200.photique.tag.presentation.validator.TagValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TagValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tag {
    String message() default "Invalid tags";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
