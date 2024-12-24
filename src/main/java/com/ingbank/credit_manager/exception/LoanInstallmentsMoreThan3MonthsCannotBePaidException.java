package com.ingbank.credit_manager.exception;

public class LoanInstallmentsMoreThan3MonthsCannotBePaidException extends RuntimeException {
    public LoanInstallmentsMoreThan3MonthsCannotBePaidException(String message) {
        super(message);
    }
}
