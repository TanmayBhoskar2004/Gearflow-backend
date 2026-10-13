package com.vst.vehicle_service_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceRequestRequest {

    @NotNull
    private Long vehicleId;

    @NotBlank
    private String description;
}