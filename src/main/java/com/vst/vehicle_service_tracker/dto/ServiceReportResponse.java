package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class ServiceReportResponse {
    private Map<String, Long> servicesByStatus;      // RECEIVED: 5, DELIVERED: 12, ...
    private Map<String, Long> monthlyServiceVolume;  // 2026-09: 4, 2026-10: 7, ...
    private Map<String, BigDecimal> monthlyRevenue;  // 2026-09: 45000, ...
    private Map<String, Long> topPartsUsed;          // Brake Pad: 12, Oil Filter: 20, ...
    private BigDecimal totalRevenue;
    private BigDecimal totalOutstanding;
}