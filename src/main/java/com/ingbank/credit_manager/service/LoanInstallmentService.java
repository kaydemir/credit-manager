package com.ingbank.credit_manager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.ingbank.credit_manager.entity.Loan;
import com.ingbank.credit_manager.entity.LoanInstallment;

public interface LoanInstallmentService {
    BigDecimal getTimeBasedInstallmentAmount(LocalDate dueDate, BigDecimal installmentAmount);
    List<LoanInstallment> listInstallmentsByLoanId(Long loanId);
    List<LoanInstallment> listInstallmentsByLoanIdAndIsPaid(Long loanId, Boolean isPaid);
    void saveLoanInstallment(Loan loan, BigDecimal totalLoanAmount, LocalDate dueDate);
    void saveAllLoanInstallments(List<LoanInstallment> installments);
}
