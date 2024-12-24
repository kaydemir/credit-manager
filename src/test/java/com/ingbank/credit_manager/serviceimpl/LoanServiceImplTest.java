package com.ingbank.credit_manager.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.ingbank.credit_manager.beans.PaymentResult;
import com.ingbank.credit_manager.entity.Customer;
import com.ingbank.credit_manager.entity.Loan;
import com.ingbank.credit_manager.entity.LoanInstallment;
import com.ingbank.credit_manager.exception.CustomerCreditLimitExceededException;
import com.ingbank.credit_manager.exception.LoanInstallmentsMoreThan3MonthsCannotBePaidException;
import com.ingbank.credit_manager.exception.LoanIsAlreadyFullyPaidException;
import com.ingbank.credit_manager.exception.NotFoundException;
import com.ingbank.credit_manager.repository.CustomerRepository;
import com.ingbank.credit_manager.repository.LoanRepository;
import com.ingbank.credit_manager.request.CreateLoanRequest;
import com.ingbank.credit_manager.request.PayLoanRequest;
import com.ingbank.credit_manager.service.LoanInstallmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanInstallmentService loanInstallmentService;

    @InjectMocks
    private LoanServiceImpl subject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateLoan() {
        Long customerId = 1L;
        BigDecimal amount = BigDecimal.valueOf(5000);
        Integer installments = 6;
        BigDecimal interestRate = BigDecimal.valueOf(0.1);

        CreateLoanRequest request = CreateLoanRequest.builder()
                .customerId(customerId)
                .amount(amount)
                .installments(installments)
                .interestRate(interestRate)
                .build();

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setCreditLimit(BigDecimal.valueOf(10000));
        customer.setUsedCreditLimit(BigDecimal.valueOf(2000));

        Loan loan = new Loan();
        loan.setId(1L);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        Loan createdLoan = subject.createLoan(request);

        assertNotNull(createdLoan);
        verify(loanRepository, times(1)).save(any(Loan.class));
        verify(loanInstallmentService, times(installments)).saveLoanInstallment(
                any(Loan.class), any(BigDecimal.class), any(LocalDate.class));
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testCreateLoanWhenCustomerNotFound() {
        Long customerId = 1L;
        CreateLoanRequest request = CreateLoanRequest.builder()
                .customerId(customerId)
                .amount(BigDecimal.valueOf(5000))
                .installments(6)
                .interestRate(BigDecimal.valueOf(0.1))
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> subject.createLoan(request));
        assertEquals("Customer not found with requested customerId: 1", exception.getMessage());
        verify(loanRepository, never()).save(any(Loan.class));
        verify(loanInstallmentService, never()).saveLoanInstallment(any(), any(), any());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void testCreateLoanWhenCustomerExceedsCreditLimit() {
        Long customerId = 1L;
        CreateLoanRequest request = CreateLoanRequest.builder()
                .customerId(customerId)
                .amount(BigDecimal.valueOf(9000))
                .installments(6)
                .interestRate(BigDecimal.valueOf(0.1))
                .build();

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setCreditLimit(BigDecimal.valueOf(10000));
        customer.setUsedCreditLimit(BigDecimal.valueOf(2000));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerCreditLimitExceededException exception = assertThrows(CustomerCreditLimitExceededException.class, () -> subject.createLoan(request));
        assertTrue(exception.getMessage().contains("Customer exceeds credit limit"));
        verify(loanRepository, never()).save(any(Loan.class));
        verify(loanInstallmentService, never()).saveLoanInstallment(any(), any(), any());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void testListLoans() {
        Long customerId = 1L;
        Loan loan = new Loan();
        Customer customer = new Customer();
        customer.setId(customerId);
        loan.setCustomer(customer);
        when(loanRepository.findByCustomerId(customerId)).thenReturn(List.of(loan));

        List<Loan> loans = subject.listLoans(customerId);
        assertNotNull(loans);
        assertEquals(customerId, loans.get(0).getCustomer().getId());
        verify(loanRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    void testListLoansWithOptionalParameters() {
        Long customerId = 1L;
        Loan loan = new Loan();
        Customer customer = new Customer();
        customer.setId(customerId);
        loan.setCustomer(customer);
        when(loanRepository.findLoans(customerId, 6, false)).thenReturn(List.of(loan));

        List<Loan> loans = subject.listLoans(customerId, 6, false);
        assertNotNull(loans);
        assertEquals(customerId, loans.get(0).getCustomer().getId());
        verify(loanRepository, times(1)).findLoans(customerId,6, false);
    }

    @Test
    void testPayLoan() {
        Long loanId = 1L;
        BigDecimal paymentAmount = BigDecimal.valueOf(400);
        PayLoanRequest request = PayLoanRequest.builder().loanId(loanId).amount(paymentAmount).build();

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setIsPaid(false);

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setId(1L);
        installment1.setLoan(loan);
        installment1.setAmount(BigDecimal.valueOf(200));
        installment1.setDueDate(LocalDate.now().plusMonths(1));
        LoanInstallment installment2 = new LoanInstallment();
        installment2.setId(2L);
        installment2.setLoan(loan);
        installment2.setAmount(BigDecimal.valueOf(200));
        installment2.setDueDate(LocalDate.now().plusMonths(2));
        List<LoanInstallment> unpaidInstallments = List.of(installment1, installment2);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentService.listInstallmentsByLoanIdAndIsPaid(loanId, false)).thenReturn(unpaidInstallments);
        when(loanInstallmentService.getTimeBasedInstallmentAmount(any(), any())).thenReturn(BigDecimal.valueOf(200), BigDecimal.valueOf(200));


        PaymentResult result = subject.payLoan(request);

        assertNotNull(result);
        assertEquals(2, result.getInstallmentsPaid());
        assertEquals(BigDecimal.valueOf(400).setScale(2, RoundingMode.HALF_UP), result.getTotalAmountSpent());
        assertTrue(result.isLoanFullyPaid());

        verify(loanRepository, times(1)).findById(loanId);
        verify(loanInstallmentService, times(1)).listInstallmentsByLoanIdAndIsPaid(loanId, false);
        verify(loanInstallmentService, times(1)).saveAllLoanInstallments(unpaidInstallments);
        verify(loanRepository, times(1)).save(loan);
    }

    @Test
    void testPayLoanWhenLoanNotFound() {
        Long loanId = 1L;
        PayLoanRequest request = PayLoanRequest.builder().loanId(loanId).amount(BigDecimal.valueOf(500)).build();
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> subject.payLoan(request));
        assertEquals("Loan not found with request loanId: " + loanId, exception.getMessage());

        verify(loanRepository, times(1)).findById(loanId);
        verifyNoInteractions(loanInstallmentService);
    }

    @Test
    void testPayLoanWhenLoanAlreadyFullyPaid() {
        Long loanId = 1L;
        PayLoanRequest request = PayLoanRequest.builder().loanId(loanId).amount(BigDecimal.valueOf(500)).build();

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setIsPaid(true);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        LoanIsAlreadyFullyPaidException exception = assertThrows(LoanIsAlreadyFullyPaidException.class, () -> subject.payLoan(request));
        assertEquals("Loan is already fully paid", exception.getMessage());

        verify(loanRepository, times(1)).findById(loanId);
        verifyNoInteractions(loanInstallmentService);
    }

    @Test
    void testPayLoanWhenNoInstallmentsPayableInNext3Months() {
        Long loanId = 1L;
        PayLoanRequest request = PayLoanRequest.builder().loanId(loanId).amount(BigDecimal.valueOf(500)).build();

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setIsPaid(false);


        LoanInstallment installment = new LoanInstallment();
        installment.setId(1L);
        installment.setLoan(loan);
        installment.setAmount(BigDecimal.valueOf(500));
        installment.setDueDate(LocalDate.now().plusMonths(4));
        installment.setIsPaid(false);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentService.listInstallmentsByLoanIdAndIsPaid(loanId, false)).thenReturn(List.of(installment));
        LoanInstallmentsMoreThan3MonthsCannotBePaidException exception = assertThrows(LoanInstallmentsMoreThan3MonthsCannotBePaidException.class, () -> subject.payLoan(request));
        assertEquals("Loan installments more than 3 calendar months cannot be paid", exception.getMessage());

        verify(loanRepository, times(1)).findById(loanId);
        verify(loanInstallmentService, times(1)).listInstallmentsByLoanIdAndIsPaid(loanId, false);
    }

    @Test
    void testPayLoanWhenPartialPayment() {
        Long loanId = 1L;
        PayLoanRequest request = PayLoanRequest.builder().loanId(loanId).amount(BigDecimal.valueOf(250)).build();

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setIsPaid(false);

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setId(1L);
        installment1.setLoan(loan);
        installment1.setAmount(BigDecimal.valueOf(200));
        installment1.setDueDate(LocalDate.now().plusMonths(1));
        LoanInstallment installment2 = new LoanInstallment();
        installment2.setId(2L);
        installment2.setLoan(loan);
        installment2.setAmount(BigDecimal.valueOf(300));
        installment2.setDueDate(LocalDate.now().plusMonths(2));

        List<LoanInstallment> unpaidInstallments = List.of(installment1, installment2);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentService.listInstallmentsByLoanIdAndIsPaid(loanId, false)).thenReturn(unpaidInstallments);
        when(loanInstallmentService.getTimeBasedInstallmentAmount(any(), any())).thenReturn(BigDecimal.valueOf(200), BigDecimal.valueOf(300));
        PaymentResult result = subject.payLoan(request);

        assertNotNull(result);
        assertEquals(1, result.getInstallmentsPaid());
        assertEquals(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), result.getTotalAmountSpent());
        assertFalse(result.isLoanFullyPaid());

        verify(loanRepository, times(1)).findById(loanId);
        verify(loanInstallmentService, times(1)).listInstallmentsByLoanIdAndIsPaid(loanId, false);
        verify(loanInstallmentService, times(1)).saveAllLoanInstallments(anyList());
        verify(loanRepository, never()).save(loan);
    }

}
