package com.vst.vehicle_service_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "technicians")
@Data
public class Technician {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String specialization;
    private boolean availability = true;
}