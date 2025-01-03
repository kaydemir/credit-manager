package com.ingbank.credit_manager.service;

import java.util.List;

import com.ingbank.credit_manager.beans.PaymentResult;
import com.ingbank.credit_manager.entity.Loan;
import com.ingbank.credit_manager.request.CreateLoanRequest;
import com.ingbank.credit_manager.request.PayLoanRequest;

public interface LoanService {
    Loan createLoan(CreateLoanRequest request);
    List<Loan> listLoans(Long customerId);
    List<Loan> listLoans(Long customerId, Integer numberOfInstallments, Boolean isPaid);
    PaymentResult payLoan(PayLoanRequest request);
    Loan findById(Long loanId);
}
