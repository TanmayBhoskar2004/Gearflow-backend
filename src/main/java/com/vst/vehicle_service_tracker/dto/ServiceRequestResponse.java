package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServiceRequestResponse {
    private Long id;
    private Long vehicleId;
    private String registrationNumber;
    private String make;
    private String model;
    private LocalDateTime requestDate;
    private String description;
    private String status;
    private String customerName;
}