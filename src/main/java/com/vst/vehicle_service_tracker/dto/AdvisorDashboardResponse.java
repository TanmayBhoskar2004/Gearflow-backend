package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.util.Map;

@Data
public class AdvisorDashboardResponse {
    private long totalCustomers;
    private long totalVehicles;
    private long pendingServiceRequests;
    private long totalJobCards;
    private Map<String, Long> jobsByStatus; // RECEIVED: 3, INSPECTION: 1, ...
    private long unpaidInvoices;
    private long lowStockParts;
}