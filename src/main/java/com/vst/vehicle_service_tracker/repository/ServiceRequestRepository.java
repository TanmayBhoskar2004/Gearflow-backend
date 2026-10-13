package com.vst.vehicle_service_tracker.repository;

import com.vst.vehicle_service_tracker.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByVehicleId(Long vehicleId);
}