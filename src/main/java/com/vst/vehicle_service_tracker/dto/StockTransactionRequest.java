package com.vst.vehicle_service_tracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockTransactionRequest {

    @NotNull
    private Long partId;

    @NotNull
    @Positive
    private Integer quantity;

    private String note; // optional, e.g., "Purchase from ABC Suppliers"
}