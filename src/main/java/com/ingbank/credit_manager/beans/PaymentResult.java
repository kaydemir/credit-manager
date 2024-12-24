package com.ingbank.credit_manager.beans;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResult {
    private int installmentsPaid;
    @Column(precision = 20, scale = 2)
    private BigDecimal totalAmountSpent;
    private boolean loanFullyPaid;
}
