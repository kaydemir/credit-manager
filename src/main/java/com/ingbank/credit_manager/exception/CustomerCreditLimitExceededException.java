package com.ingbank.credit_manager.exception;

public class CustomerCreditLimitExceededException extends RuntimeException {
    public CustomerCreditLimitExceededException(String message) {
        super(message);
    }
}
