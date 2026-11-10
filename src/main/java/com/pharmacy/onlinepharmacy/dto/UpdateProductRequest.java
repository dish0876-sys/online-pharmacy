package com.pharmacy.onlinepharmacy.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private boolean prescriptionRequired;
    // Остаток (stockQuantity) вынесем в отдельный эндпоинт
}