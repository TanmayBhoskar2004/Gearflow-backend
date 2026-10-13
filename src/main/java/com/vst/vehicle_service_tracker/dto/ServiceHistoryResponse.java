package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ServiceHistoryResponse {
    private Long jobCardId;
    private String registrationNumber;
    private String vehicleDescription;
    private String serviceDescription;
    private LocalDateTime receivedDate;
    private LocalDateTime deliveredDate;
    private String finalStatus;
    private BigDecimal invoiceTotal;
    private String paymentStatus;
    private List<PartUsedResponse> partsUsed;
}