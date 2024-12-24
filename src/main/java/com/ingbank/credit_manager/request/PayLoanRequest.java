package com.ingbank.credit_manager.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayLoanRequest {
    @NotNull(message = "Loan ID is required")
    private Long loanId;
    @NotNull(message = "Loan amount is required")
    @Positive(message = "Loan amount must be positive")
    @Digits(integer = 20, fraction = 2, message = "Amount must be a valid value with up to 20 digits and 2 decimals")
    private BigDecimal amount;
}
