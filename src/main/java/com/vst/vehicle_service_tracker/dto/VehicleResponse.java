package com.vst.vehicle_service_tracker.dto;

import lombok.Data;

@Data
public class VehicleResponse {
    private Long id;
    private String registrationNumber;
    private String make;
    private String model;
    private Integer year;
    private String vin;
    private Long customerId;
    private String customerName;
}