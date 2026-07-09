package com.enterprise.eakip.core.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = IsbnValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Isbn {
    String message() default "Invalid ISBN format. Must be a valid ISBN-10 or ISBN-13";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
