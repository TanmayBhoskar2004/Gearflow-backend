package com.vst.vehicle_service_tracker.repository;

import com.vst.vehicle_service_tracker.entity.JobCard;
import com.vst.vehicle_service_tracker.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobCardRepository extends JpaRepository<JobCard, Long> {
    List<JobCard> findByStatus(JobStatus status);
    List<JobCard> findByAdvisorId(Long advisorId);
}