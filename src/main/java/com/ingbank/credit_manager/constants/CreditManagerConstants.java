package com.ingbank.credit_manager.constants;

import java.math.BigDecimal;

public class CreditManagerConstants {

    private CreditManagerConstants() {
        // private contructor to prevent init
    }
    // general usages
    public static final String RETURNING_RESPONSE = "Returning response {}";
    // loans
    public static final String LOANS_ENDPOINT = "/api/v1/loans";
    // loan installment
    public static final String LOAN_INSTALLMENTS_ENDPOINT = "/api/v1/loans/installments";
    public static final BigDecimal PAYMENT_TIMING_BASED_INTEREST_RATE = new BigDecimal("0.001");
}
