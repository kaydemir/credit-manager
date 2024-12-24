package com.ingbank.credit_manager.validator;

import java.util.Arrays;
import java.util.List;

import com.ingbank.credit_manager.validator.annotation.ValidInstallments;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InstallmentsValidator implements ConstraintValidator<ValidInstallments, Integer> {
    private final List<Integer> validInstallments = Arrays.asList(6, 9, 12, 24);

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && validInstallments.contains(value);
    }
}
