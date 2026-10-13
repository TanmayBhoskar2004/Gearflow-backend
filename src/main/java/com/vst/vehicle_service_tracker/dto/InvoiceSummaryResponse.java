package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InvoiceSummaryResponse {
    private Long invoiceId;
    private String registrationNumber;
    private BigDecimal total;
    private String paymentStatus;
}