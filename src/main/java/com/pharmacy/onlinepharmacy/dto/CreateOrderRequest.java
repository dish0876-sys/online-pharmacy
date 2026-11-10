package com.pharmacy.onlinepharmacy.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String address;
    private String paymentMethod;

    // ID рецепта (если в корзине есть рецептурные товары)
    private Long prescriptionId;
}