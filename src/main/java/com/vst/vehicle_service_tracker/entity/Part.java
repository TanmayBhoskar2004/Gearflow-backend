package com.vst.vehicle_service_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "parts")
@Data
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String brand;
    private BigDecimal unitPrice;
    private Integer warrantyMonths;
    private Integer stockQuantity = 0;
}