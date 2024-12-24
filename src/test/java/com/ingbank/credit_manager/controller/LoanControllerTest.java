package com.ingbank.credit_manager.controller;

import com.ingbank.credit_manager.beans.PaymentResult;
import com.ingbank.credit_manager.entity.Loan;
import com.ingbank.credit_manager.exception.CustomerCreditLimitExceededException;
import com.ingbank.credit_manager.exception.LoanInstallmentsMoreThan3MonthsCannotBePaidException;
import com.ingbank.credit_manager.exception.LoanIsAlreadyFullyPaidException;
import com.ingbank.credit_manager.exception.NotFoundException;
import com.ingbank.credit_manager.request.CreateLoanRequest;
import com.ingbank.credit_manager.request.PayLoanRequest;
import com.ingbank.credit_manager.response.CreateLoanResponse;
import com.ingbank.credit_manager.response.PayLoanResponse;
import com.ingbank.credit_manager.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController subject;

    private CreateLoanRequest createLoanRequest;
    private PayLoanRequest payLoanRequest;

    @BeforeEach
    void setup() {
        createLoanRequest = CreateLoanRequest.builder().amount(new BigDecimal("1000.00")).customerId(1L).installments(12).build();
        payLoanRequest = PayLoanRequest.builder().loanId(1L).amount(new BigDecimal("500.00")).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateLoan() {
        Loan mockLoan = new Loan();
        mockLoan.setId(1L);

        when(loanService.createLoan(any(CreateLoanRequest.class))).thenReturn(mockLoan);

        ResponseEntity<CreateLoanResponse> response = subject.createLoan(createLoanRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getLoan().getId());
    }

    @Test
    void testCreateLoanWhenCreditLimitExceeded() {
        when(loanService.createLoan(any(CreateLoanRequest.class)))
                .thenThrow(new CustomerCreditLimitExceededException("Credit limit exceeded"));

        ResponseEntity<CreateLoanResponse> response = subject.createLoan(createLoanRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrorMessage().contains("Credit limit exceeded"));
    }

    @Test
    void testListLoans() {
        Loan mockLoan = new Loan();
        mockLoan.setId(1L);

        when(loanService.listLoans(anyLong(), anyInt(), anyBoolean())).thenReturn(List.of(mockLoan));

        ResponseEntity<List<Loan>> response = subject.listLoans(1L, 10, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void testPayLoan() {
        PaymentResult paymentResult = PaymentResult.builder().loanFullyPaid(true).build();

        when(loanService.payLoan(any(PayLoanRequest.class))).thenReturn(paymentResult);

        ResponseEntity<PayLoanResponse> response = subject.payLoan(payLoanRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getPaymentResult().isLoanFullyPaid());
    }

    @Test
    void testPayLoanWhenLoanISAlreadyFullyPaid() {
        when(loanService.payLoan(any(PayLoanRequest.class)))
                .thenThrow(new LoanIsAlreadyFullyPaidException("Loan is already fully paid"));

        ResponseEntity<PayLoanResponse> response = subject.payLoan(payLoanRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getErrorMessage().contains("Loan is already fully paid"));
    }

    @Test
    void testPayLoanInstallmentsMoreThan3Months() {
        when(loanService.payLoan(any(PayLoanRequest.class)))
                .thenThrow(new LoanInstallmentsMoreThan3MonthsCannotBePaidException("Installments cannot be paid"));

        ResponseEntity<PayLoanResponse> response = subject.payLoan(payLoanRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getErrorMessage().contains("Installments cannot be paid"));
    }

    @Test
    void testPayLoanWhenLoanNotFound() {
        when(loanService.payLoan(any(PayLoanRequest.class)))
                .thenThrow(new NotFoundException("Loan not found"));

        ResponseEntity<PayLoanResponse> response = subject.payLoan(payLoanRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getErrorMessage().contains("Loan not found"));
    }
}
