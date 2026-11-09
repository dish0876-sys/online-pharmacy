package com.pharmacy.onlinepharmacy.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private boolean prescriptionRequired;
    private Integer stockQuantity;
}