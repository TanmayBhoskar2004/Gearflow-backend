package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServiceStatusHistoryResponse {
    private Long id;
    private String status;
    private String changedByUsername;
    private LocalDateTime changedAt;
}