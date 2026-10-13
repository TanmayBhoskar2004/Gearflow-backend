package com.vst.vehicle_service_tracker.repository;

import com.vst.vehicle_service_tracker.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findByPartId(Long partId);
    List<InventoryTransaction> findByJobCardId(Long jobCardId);
}