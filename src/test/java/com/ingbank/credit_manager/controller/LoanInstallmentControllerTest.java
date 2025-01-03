package com.ingbank.credit_manager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.ingbank.credit_manager.entity.Customer;
import com.ingbank.credit_manager.entity.Loan;
import com.ingbank.credit_manager.entity.LoanInstallment;
import com.ingbank.credit_manager.service.LoanInstallmentService;
import com.ingbank.credit_manager.service.LoanService;
import com.ingbank.credit_manager.util.AuthorizationComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

class LoanInstallmentControllerTest {

    @Mock
    private LoanInstallmentService loanInstallmentService;

    @Mock
    private LoanService loanService;

    @Mock
    AuthorizationComponent authorizationComponent;

    @InjectMocks
    private LoanInstallmentController loanInstallmentController;

    private LoanInstallment loanInstallment1;
    private LoanInstallment loanInstallment2;

    @BeforeEach
    public void setUp() {
        loanInstallment1 = new LoanInstallment();
        loanInstallment1.setId(1L);
        loanInstallment1.setAmount(new BigDecimal("1000.0"));

        loanInstallment2 = new LoanInstallment();
        loanInstallment2.setId(2L);
        loanInstallment2.setAmount(new BigDecimal("2000.0"));
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testListInstallmentsByLoanId() {
        List<LoanInstallment> mockInstallments = Arrays.asList(loanInstallment1, loanInstallment2);
        when(loanInstallmentService.listInstallmentsByLoanId(1L)).thenReturn(mockInstallments);
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setCustomer(new Customer());
        when(loanService.findById(1L)).thenReturn(loan);
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("admin");
        ResponseEntity<List<LoanInstallment>> response = loanInstallmentController.listInstallmentsByLoanId(1L, mockAuth);

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(loanInstallment1, response.getBody().get(0));
        assertEquals(loanInstallment2, response.getBody().get(1));

        verify(loanInstallmentService, times(1)).listInstallmentsByLoanId(1L);
    }

    @Test
    void testListInstallmentsByLoanIdWhenNoInstallmentsFound() {
        when(loanInstallmentService.listInstallmentsByLoanId(1L)).thenReturn(List.of());

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setCustomer(new Customer());
        when(loanService.findById(1L)).thenReturn(loan);
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("admin");
        ResponseEntity<List<LoanInstallment>> response = loanInstallmentController.listInstallmentsByLoanId(1L, mockAuth);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(loanInstallmentService, times(1)).listInstallmentsByLoanId(1L);
    }
}

