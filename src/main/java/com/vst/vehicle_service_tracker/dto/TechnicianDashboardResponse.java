package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.util.Map;

@Data
public class TechnicianDashboardResponse {
    private long assignedJobCount;
    private Map<String, Long> myJobsByStatus; // RECEIVED: 1, REPAIRING: 2, ...
}