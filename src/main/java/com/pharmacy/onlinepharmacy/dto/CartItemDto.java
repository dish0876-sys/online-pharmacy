package com.pharmacy.onlinepharmacy.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CartItemDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;      // Цена за 1 штуку
    private BigDecimal subtotal;   // Общая стоимость этой позиции (цена * количество)
}