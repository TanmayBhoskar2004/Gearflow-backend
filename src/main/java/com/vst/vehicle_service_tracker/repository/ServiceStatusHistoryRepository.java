package com.vst.vehicle_service_tracker.repository;

import com.vst.vehicle_service_tracker.entity.ServiceStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceStatusHistoryRepository extends JpaRepository<ServiceStatusHistory, Long> {
    List<ServiceStatusHistory> findByJobCardIdOrderByChangedAtDesc(Long jobCardId);
}