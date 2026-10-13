package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class InventoryDashboardResponse {
    private long totalParts;
    private long totalStockQuantity;
    private BigDecimal totalInventoryValue;
    private long lowStockCount;
    private List<PartResponse> lowStockParts;
}