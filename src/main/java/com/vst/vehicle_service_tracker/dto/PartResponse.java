package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PartResponse {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal unitPrice;
    private Integer warrantyMonths;
    private Integer stockQuantity;
}