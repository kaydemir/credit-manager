package com.ingbank.credit_manager.controller;

import java.util.List;

import com.ingbank.credit_manager.constants.CreditManagerConstants;
import com.ingbank.credit_manager.entity.LoanInstallment;
import com.ingbank.credit_manager.service.LoanInstallmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = CreditManagerConstants.LOAN_INSTALLMENTS_ENDPOINT, produces = {MediaType.APPLICATION_JSON_VALUE})
public class LoanInstallmentController {

    private final LoanInstallmentService loanInstallmentService;

    @Autowired
    public LoanInstallmentController(LoanInstallmentService loanInstallmentService) {
        this.loanInstallmentService = loanInstallmentService;
        log.trace("{} initialized", this.getClass().getName());
    }

    @GetMapping(value = "/{loanId}")
    @Operation(summary = "Get all installments for a loan", description = "Retrieves a list of all installments for the loan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved installments"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<List<LoanInstallment>> listInstallmentsByLoanId(@PathVariable Long loanId) {
        log.debug("GET /api/v1/loans/installments/{loanId}: listInstallmentsByLoanId({})", loanId);
        List<LoanInstallment> loanInstallments = loanInstallmentService.listInstallmentsByLoanId(loanId);
        log.debug("GET /api/v1/loans/installments/{loanId}: " + CreditManagerConstants.RETURNING_RESPONSE, loanInstallments);
        return ResponseEntity.ok(loanInstallments);
    }
}
