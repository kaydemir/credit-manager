package com.ingbank.credit_manager.request;

import java.math.BigDecimal;

import com.ingbank.credit_manager.validator.annotation.ValidInstallments;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateLoanRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Loan amount is required")
    @Positive(message = "Loan amount must be positive")
    @Digits(integer = 20, fraction = 2, message = "Amount must be a valid value with up to 20 digits and 2 decimals")
    private BigDecimal amount;

    @NotNull(message = "Interest rate is required")
    @Positive(message = "Interest rate must be positive")
    @Digits(integer = 5, fraction = 2, message = "Interest rate must be a valid value with up to 5 digits and 2 decimals")
    @DecimalMin(value = "0.1", message = "Interest rate must be at least 0.1")
    @DecimalMax(value = "0.5", message = "Interest rate must be at most 0.5")
    private BigDecimal interestRate;

    @NotNull(message = "Number of installments is required")
    @ValidInstallments
    private Integer installments;
}
