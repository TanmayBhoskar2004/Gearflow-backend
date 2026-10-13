package com.vst.vehicle_service_tracker.repository;

import com.vst.vehicle_service_tracker.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    List<Technician> findByAvailabilityTrue();
}