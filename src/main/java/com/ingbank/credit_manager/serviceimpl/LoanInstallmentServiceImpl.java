package com.ingbank.credit_manager.serviceimpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import com.ingbank.credit_manager.constants.CreditManagerConstants;
import com.ingbank.credit_manager.entity.Loan;
import com.ingbank.credit_manager.entity.LoanInstallment;
import com.ingbank.credit_manager.repository.LoanInstallmentRepository;
import com.ingbank.credit_manager.service.LoanInstallmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoanInstallmentServiceImpl implements LoanInstallmentService {

    private final LoanInstallmentRepository loanInstallmentRepository;

    @Autowired
    public LoanInstallmentServiceImpl(LoanInstallmentRepository loanInstallmentRepository) {
        this.loanInstallmentRepository = loanInstallmentRepository;
        log.trace("{} initialized", this.getClass().getName());
    }

    @Override
    public BigDecimal getTimeBasedInstallmentAmount(LocalDate dueDate, BigDecimal installmentAmount) {
        if (installmentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Installment amount cannot be less than zero '{}'", installmentAmount);
            return BigDecimal.ZERO;
        }
        LocalDate now = LocalDate.now();
        long daysDifference = java.time.temporal.ChronoUnit.DAYS.between(dueDate, now);
        BigDecimal daysDiff = BigDecimal.valueOf(daysDifference);
        if (daysDifference != 0) {
            installmentAmount = installmentAmount.add(installmentAmount.multiply(CreditManagerConstants.PAYMENT_TIMING_BASED_INTEREST_RATE).multiply(daysDiff));
        }
        return installmentAmount.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<LoanInstallment> listInstallmentsByLoanId(Long loanId) {
        return loanInstallmentRepository.findByLoanId(loanId);
    }

    @Override
    public List<LoanInstallment> listInstallmentsByLoanIdAndIsPaid(Long loanId, Boolean isPaid) {
        return loanInstallmentRepository.findByLoanIdAndIsPaid(loanId, false);
    }

    @Override
    public void saveLoanInstallment(Loan loan, BigDecimal totalLoanAmount, LocalDate dueDate) {
        LoanInstallment installment = new LoanInstallment();
        installment.setLoan(loan);
        installment.setAmount(totalLoanAmount);
        installment.setDueDate(dueDate);
        loanInstallmentRepository.save(installment);
    }

    @Override
    public void saveAllLoanInstallments(List<LoanInstallment> installments) {
        loanInstallmentRepository.saveAll(installments);
    }
}
