package com.ingbank.credit_manager.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ingbank.credit_manager.beans.PaymentResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayLoanResponse {
    @JsonProperty("payLoanResult")
    private PaymentResult paymentResult;
    private String errorMessage;
}
