package com.vst.vehicle_service_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VehicleRequest {

    @NotBlank
    private String registrationNumber;

    private String make;
    private String model;
    private Integer year;
    private String vin;
}