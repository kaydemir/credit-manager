package com.ingbank.credit_manager.repository;

import java.util.List;

import com.ingbank.credit_manager.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    List<LoanInstallment> findByLoanId(Long loanId);
    List<LoanInstallment> findByLoanIdAndIsPaid(Long loanId, Boolean isPaid);
}
