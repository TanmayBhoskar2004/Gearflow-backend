package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobCardResponse {
    private Long id;
    private Long serviceRequestId;
    private String registrationNumber;
    private String customerName;
    private String advisorUsername;
    private LocalDateTime receivedDate;
    private LocalDateTime estimatedCompletion;
    private String status;
    private BigDecimal laborCharge;
    private BigDecimal otherCharges;
    private BigDecimal tax;
    private BigDecimal discount;
    private String description;
}