package com.vst.vehicle_service_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "job_cards")
@Data
public class JobCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_request_id")
    private ServiceRequest serviceRequest;

    @ManyToOne
    @JoinColumn(name = "advisor_id")
    private User advisor;

    private LocalDateTime receivedDate;
    private LocalDateTime estimatedCompletion;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private BigDecimal laborCharge;
    private BigDecimal otherCharges;
    private BigDecimal tax;
    private BigDecimal discount;
}