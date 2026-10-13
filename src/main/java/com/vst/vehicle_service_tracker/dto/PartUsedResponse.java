package com.vst.vehicle_service_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PartUsedResponse {
    private String partName;
    private String brand;
    private Integer quantityUsed;
    private BigDecimal unitPrice;
    private String warrantyNotes;
}