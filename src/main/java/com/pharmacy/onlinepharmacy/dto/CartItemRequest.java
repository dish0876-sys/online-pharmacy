package com.pharmacy.onlinepharmacy.dto;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long productId;  // Какой товар добавляем
    private Integer quantity; // Сколько штук
}
