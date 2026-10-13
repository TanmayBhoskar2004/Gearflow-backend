package com.vst.vehicle_service_tracker.repository;

import com.vst.vehicle_service_tracker.entity.JobTechnician;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobTechnicianRepository extends JpaRepository<JobTechnician, Long> {
    List<JobTechnician> findByTechnicianId(Long technicianId);
    List<JobTechnician> findByJobCardId(Long jobCardId);
}