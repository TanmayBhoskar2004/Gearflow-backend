package com.vst.vehicle_service_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    private String make;
    private String model;
    private Integer year;
    private String vin;
}