package com.courtai.advocate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {
    @NotNull(message = "Case UUID is required")
    private String caseUuid;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private String paymentMethod;
}
