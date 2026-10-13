package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.ServiceRequestRequest;
import com.vst.vehicle_service_tracker.dto.ServiceRequestResponse;

import java.util.List;

public interface ServiceRequestService {
    ServiceRequestResponse createRequest(String username, ServiceRequestRequest request);
    List<ServiceRequestResponse> getMyRequests(String username);
    List<ServiceRequestResponse> getAllRequests();
    ServiceRequestResponse getRequestById(Long id);
    List<ServiceRequestResponse> getPendingRequests();
}