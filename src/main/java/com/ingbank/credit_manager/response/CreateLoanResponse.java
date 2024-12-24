package com.ingbank.credit_manager.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ingbank.credit_manager.entity.Loan;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateLoanResponse {
    @JsonProperty("loanCreated")
    private Loan loan;
    private String errorMessage;
}
