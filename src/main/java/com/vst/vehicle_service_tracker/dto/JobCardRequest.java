package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobCardRequest {

    private Long serviceRequestId;

    private LocalDateTime estimatedCompletion;

    private BigDecimal laborCharge;
    private BigDecimal otherCharges;
    private BigDecimal tax;
    private BigDecimal discount;
}