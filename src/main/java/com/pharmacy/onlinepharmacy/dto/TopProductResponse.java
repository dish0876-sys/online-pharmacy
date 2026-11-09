package com.pharmacy.onlinepharmacy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductResponse {
    private String productName;
    private Long totalSold; // Сколько штук продано
}