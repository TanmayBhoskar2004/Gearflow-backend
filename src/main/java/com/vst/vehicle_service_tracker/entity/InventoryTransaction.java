package com.vst.vehicle_service_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions")
@Data
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "part_id")
    private Part part;

    private Integer quantityChange;
    private String transactionType;

    @ManyToOne
    @JoinColumn(name = "job_card_id")
    private JobCard jobCard;

    private LocalDateTime transactionDate;
}