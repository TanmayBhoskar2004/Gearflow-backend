package com.vst.vehicle_service_tracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull
    private Long invoiceId;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String method; // CASH, CARD, UPI

    private String reference;
}