package com.ingbank.credit_manager.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import com.ingbank.credit_manager.constants.CreditManagerConstants;
import com.ingbank.credit_manager.entity.Loan;
import com.ingbank.credit_manager.entity.LoanInstallment;
import com.ingbank.credit_manager.repository.LoanInstallmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LoanInstallmentServiceImplTest {

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @InjectMocks
    private LoanInstallmentServiceImpl subject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetTimeBasedInstallmentAmountWhenAmountReward() {
        LocalDate dueDate = LocalDate.now().minusDays(10);
        BigDecimal installmentAmount = BigDecimal.valueOf(1000);
        BigDecimal expectedAmount = installmentAmount.add(
                installmentAmount.multiply(CreditManagerConstants.PAYMENT_TIMING_BASED_INTEREST_RATE)
                        .multiply(BigDecimal.valueOf(10))
        ).setScale(2, RoundingMode.HALF_UP);

        BigDecimal result = subject.getTimeBasedInstallmentAmount(dueDate, installmentAmount);

        assertEquals(expectedAmount, result);
    }

    @Test
    void testGetTimeBasedInstallmentAmountWhenAmountPenalty() {
        LocalDate dueDate = LocalDate.now().plusDays(10);
        BigDecimal installmentAmount = BigDecimal.valueOf(1000);
        BigDecimal expectedAmount = installmentAmount.subtract(
                installmentAmount.multiply(CreditManagerConstants.PAYMENT_TIMING_BASED_INTEREST_RATE)
                        .multiply(BigDecimal.valueOf(10))
        ).setScale(2, RoundingMode.HALF_UP);

        BigDecimal result = subject.getTimeBasedInstallmentAmount(dueDate, installmentAmount);

        assertEquals(expectedAmount, result);
    }

    @Test
    void testGetTimeBasedInstallmentAmountWhenInstallmentAmountIsZero() {
        LocalDate dueDate = LocalDate.now();
        BigDecimal installmentAmount = BigDecimal.ZERO;
        BigDecimal result = subject.getTimeBasedInstallmentAmount(dueDate, installmentAmount);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testListInstallmentsByLoanId() {
        Long loanId = 1L;
        LoanInstallment installment = new LoanInstallment();
        when(loanInstallmentRepository.findByLoanId(loanId)).thenReturn(List.of(installment));
        List<LoanInstallment> result = subject.listInstallmentsByLoanId(loanId);
        assertEquals(1, result.size());
        verify(loanInstallmentRepository, times(1)).findByLoanId(loanId);
    }

    @Test
    void testListInstallmentsByLoanIdAndIsPaid() {
        Long loanId = 1L;
        LoanInstallment installment = new LoanInstallment();
        when(loanInstallmentRepository.findByLoanIdAndIsPaid(loanId, false)).thenReturn(List.of(installment));
        List<LoanInstallment> result = subject.listInstallmentsByLoanIdAndIsPaid(loanId, false);
        assertEquals(1, result.size());
        verify(loanInstallmentRepository, times(1)).findByLoanIdAndIsPaid(loanId, false);
    }

    @Test
    void testSaveLoanInstallment() {
        Loan loan = new Loan();
        BigDecimal totalLoanAmount = BigDecimal.valueOf(1000);
        LocalDate dueDate = LocalDate.now().plusMonths(1);

        // when
        subject.saveLoanInstallment(loan, totalLoanAmount, dueDate);

        ArgumentCaptor<LoanInstallment> captor = ArgumentCaptor.forClass(LoanInstallment.class);
        verify(loanInstallmentRepository, times(1)).save(captor.capture());
        LoanInstallment savedInstallment = captor.getValue();
        assertEquals(loan, savedInstallment.getLoan());
        assertEquals(totalLoanAmount, savedInstallment.getAmount());
        assertEquals(dueDate, savedInstallment.getDueDate());
    }

    @Test
    void testSaveAllLoanInstallments() {
        LoanInstallment installment = new LoanInstallment();
        List<LoanInstallment> installments = List.of(installment);
        subject.saveAllLoanInstallments(installments);
        verify(loanInstallmentRepository, times(1)).saveAll(installments);
    }
}
