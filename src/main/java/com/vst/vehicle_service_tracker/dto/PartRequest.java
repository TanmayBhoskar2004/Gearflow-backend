package com.vst.vehicle_service_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PartRequest {

    @NotBlank
    private String name;

    private String brand;

    @NotNull
    @Positive
    private BigDecimal unitPrice;

    private Integer warrantyMonths;

    private Integer stockQuantity;
}