package com.pharmacy.onlinepharmacy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SalesReportResponse {
    private BigDecimal totalRevenue; // Общая выручка
    private Long totalOrders;        // Количество заказов
}