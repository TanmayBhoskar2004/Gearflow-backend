package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceResponse {
    private Long id;
    private Long jobCardId;
    private String registrationNumber;
    private String customerName;
    private LocalDateTime invoiceDate;
    private BigDecimal laborCharge;
    private BigDecimal partsCharge;
    private BigDecimal otherCharges;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;
    private boolean paid;
    private String paymentStatus;
    private List<InvoiceItemResponse> items;
}