package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class AdminDashboardResponse {
    private long totalUsers;
    private long totalCustomers;
    private long totalTechnicians;
    private long totalVehicles;
    private long totalJobCards;
    private Map<String, Long> usersByRole;
    private BigDecimal totalRevenue;
    private BigDecimal totalOutstanding;
    private long lowStockParts;
}