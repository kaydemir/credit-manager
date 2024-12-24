package com.ingbank.credit_manager.exception;

public class LoanIsAlreadyFullyPaidException extends RuntimeException {
    public LoanIsAlreadyFullyPaidException(String message) {
        super(message);
    }
}
