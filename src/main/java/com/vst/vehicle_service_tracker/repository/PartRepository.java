package com.vst.vehicle_service_tracker.repository;

import com.vst.vehicle_service_tracker.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {
    List<Part> findByStockQuantityLessThan(int threshold);
}