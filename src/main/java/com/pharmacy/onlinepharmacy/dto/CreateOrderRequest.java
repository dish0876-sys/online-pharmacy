package com.pharmacy.onlinepharmacy.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String address;

    // Пока мы принимаем способ оплаты просто как строку (например, "CARD" или "CASH").
    // На будущих этапах мы свяжем это со справочником /payment-methods.
    private String paymentMethod;
}