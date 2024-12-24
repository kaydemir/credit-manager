package com.ingbank.credit_manager.controller;

import java.util.List;

import com.ingbank.credit_manager.beans.PaymentResult;
import com.ingbank.credit_manager.constants.CreditManagerConstants;
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
import com.ingbank.credit_manager.util.AuthorizationComponent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling loan operations.
 * All endpoints under the base path produces JSON responses.
 */
@Slf4j
@RestController
@RequestMapping(value = CreditManagerConstants.LOANS_ENDPOINT, produces = {MediaType.APPLICATION_JSON_VALUE})
public class LoanController {

    private final LoanService loanService;
    private final AuthorizationComponent authorizationComponent;

    @Autowired
    public LoanController(LoanService loanService,
                          AuthorizationComponent authorizationComponent) {
        this.loanService = loanService;
        this.authorizationComponent = authorizationComponent;
        log.trace("{} initialized", this.getClass().getName());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create loan for customer", description = "Creates a loan for the given customer, amount, interest rate with the provided installments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created loan"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<CreateLoanResponse> createLoan(@Valid @NotNull @RequestBody CreateLoanRequest request,
                                                         Authentication authentication) {
        authorizationComponent.checkAccess(request.getCustomerId(), authentication);
        CreateLoanResponse response;
        CreateLoanResponse.CreateLoanResponseBuilder builder = CreateLoanResponse.builder();
        try {
            log.debug("POST /api/v1/loans: createLoan({})", request);
            Loan loan = loanService.createLoan(request);
            response = builder.loan(loan).build();
            log.debug("POST /api/v1/loans: " + CreditManagerConstants.RETURNING_RESPONSE, response);
            return ResponseEntity.ok(response);
        } catch (CustomerCreditLimitExceededException e) {
            log.error("Customer Credit limit exceeded: {}", e.getMessage());
            response = builder.errorMessage("Customer Credit limit exceeded: " + e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (NotFoundException ex){
            log.error("Customer not found: {}", ex.getMessage());
            response = builder.errorMessage("Customer not found: " + ex.getMessage()).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping(value = "/{customerId}")
    @Operation(summary = "Get all loans for customer", description = "Retrieves a list of all loans.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved loans"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<List<Loan>> listLoans(@PathVariable Long customerId,
                                                @RequestParam(required = false) Integer numberOfInstallments,
                                                @RequestParam(required = false) Boolean isPaid,
                                                Authentication authentication) {
        authorizationComponent.checkAccess(customerId, authentication);
        log.debug("GET /api/v1/loans/{customerId}: listLoans({},{},{})", customerId, numberOfInstallments, isPaid);
        List<Loan> loansList = loanService.listLoans(customerId, numberOfInstallments, isPaid);
        log.debug("GET /api/v1/loans/{customerId}: " + CreditManagerConstants.RETURNING_RESPONSE, loansList);
        return ResponseEntity.ok(loansList);
    }

    @PostMapping(value = "/pay", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Pay installment for a loan", description = "Pays installment for a loan with the given amount ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved installments"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<PayLoanResponse> payLoan(@Valid @NotNull @RequestBody PayLoanRequest request) {
        PayLoanResponse response;
        PayLoanResponse.PayLoanResponseBuilder builder = PayLoanResponse.builder();
        try {
            log.debug("POST /api/v1/loans/pay: payLoan({})", request);
            PaymentResult paymentResult = loanService.payLoan(request);
            log.debug("POST /api/v1/loans/pay: " + CreditManagerConstants.RETURNING_RESPONSE, paymentResult);
            response = builder.paymentResult(paymentResult).build();
            return ResponseEntity.ok(response);
        } catch (LoanIsAlreadyFullyPaidException e) {
            log.error("Loan is already fully paid: {}", e.getMessage());
            response = builder.errorMessage(e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (LoanInstallmentsMoreThan3MonthsCannotBePaidException e) {
            log.error("Loan installments more than 3 months cannot be paid: {}", e.getMessage());
            response = builder.errorMessage(e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (NotFoundException ex) {
            log.error("Loan not found: {}", ex.getMessage());
            response = builder.errorMessage(ex.getMessage()).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
