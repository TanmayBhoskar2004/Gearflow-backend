package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryTransactionResponse {
    private Long id;
    private Long partId;
    private String partName;
    private Integer quantityChange;
    private String transactionType;
    private Long jobCardId;
    private LocalDateTime transactionDate;
}