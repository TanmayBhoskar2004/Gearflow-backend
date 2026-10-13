package com.vst.vehicle_service_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobStatusUpdateRequest {

    @NotBlank
    private String status; // RECEIVED, INSPECTION, REPAIRING, etc.

    private String note; // optional note
}