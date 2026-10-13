package com.vst.vehicle_service_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "invoices")
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_card_id")
    private JobCard jobCard;

    private LocalDateTime invoiceDate;
    private BigDecimal laborCharge;
    private BigDecimal partsCharge;
    private BigDecimal otherCharges;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;
    private boolean paid = false;
    private String paymentStatus = "UNPAID";
}