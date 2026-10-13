package com.vst.vehicle_service_tracker.repository;

import com.vst.vehicle_service_tracker.entity.JobPart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobPartRepository extends JpaRepository<JobPart, Long> {
    List<JobPart> findByJobCardId(Long jobCardId);
}