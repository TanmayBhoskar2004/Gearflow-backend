package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.PartRequest;
import com.vst.vehicle_service_tracker.dto.PartResponse;

import java.util.List;

public interface PartService {
    PartResponse createPart(PartRequest request);
    PartResponse updatePart(Long id, PartRequest request);
    PartResponse getPartById(Long id);
    List<PartResponse> getAllParts();
    List<PartResponse> getLowStockParts(int threshold);
    void deletePart(Long id);
}