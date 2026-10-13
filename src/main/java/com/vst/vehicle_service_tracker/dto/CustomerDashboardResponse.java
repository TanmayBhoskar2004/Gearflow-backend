package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CustomerDashboardResponse {
    private long vehicleCount;
    private long activeServiceCount;
    private long completedServiceCount;
    private long unpaidInvoiceCount;
    private BigDecimal outstandingAmount;
    private long unreadNotificationCount;
}