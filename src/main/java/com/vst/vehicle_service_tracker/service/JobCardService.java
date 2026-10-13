package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.JobCardRequest;
import com.vst.vehicle_service_tracker.dto.JobCardResponse;
import com.vst.vehicle_service_tracker.dto.ServiceStatusHistoryResponse;

import java.util.List;

public interface JobCardService {

    // ==================== DAY 4 ====================
    JobCardResponse createJobCard(String advisorUsername, JobCardRequest request);
    List<JobCardResponse> getAllJobCards();
    JobCardResponse getJobCardById(Long id);
    List<JobCardResponse> getJobCardsByStatus(String status);

    // ==================== DAY 5 ====================
    JobCardResponse assignTechnician(Long jobCardId, Long technicianId, String advisorUsername);
    JobCardResponse updateStatus(Long jobCardId, String newStatus, String username, String note);
    List<ServiceStatusHistoryResponse> getStatusHistory(Long jobCardId);
    List<JobCardResponse> getMyAssignedJobs(String technicianUsername);
}