package com.vst.vehicle_service_tracker.repository;

import com.vst.vehicle_service_tracker.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByJobCardId(Long jobCardId);
}