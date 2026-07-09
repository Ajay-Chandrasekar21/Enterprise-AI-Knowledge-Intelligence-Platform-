package com.enterprise.eakip.core.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {

    private static final Pattern ISBN_PATTERN = Pattern.compile(
            "^(?:ISBN(?:-10)?:?\\s*)?([0-9Xx]{10})$|^(?:ISBN(?:-13)?:?\\s*)?([0-9]{13})$"
    );

    @Override
    public void initialize(Isbn constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Use @NotEmpty or @NotNull for null validation
        }

        String cleanValue = value.replaceAll("[- ]", "");
        return ISBN_PATTERN.matcher(cleanValue).matches();
    }
}
