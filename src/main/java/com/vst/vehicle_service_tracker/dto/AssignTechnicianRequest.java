package com.vst.vehicle_service_tracker.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignTechnicianRequest {

    @NotNull
    private Long technicianId;
}