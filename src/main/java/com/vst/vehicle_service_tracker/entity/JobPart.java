package com.vst.vehicle_service_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "job_parts")
@Data
public class JobPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_card_id")
    private JobCard jobCard;

    @ManyToOne
    @JoinColumn(name = "part_id")
    private Part part;

    private Integer quantityUsed;
    private BigDecimal unitPriceAtTime;

    @Lob
    private String warrantyNotes;
}