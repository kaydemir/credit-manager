package com.ingbank.credit_manager.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ingbank.credit_manager.validator.InstallmentsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = InstallmentsValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInstallments {
    String message() default "Installments must be one of [6, 9, 12, 24]";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
