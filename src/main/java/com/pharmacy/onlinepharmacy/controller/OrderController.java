package com.pharmacy.onlinepharmacy.controller;

import com.pharmacy.onlinepharmacy.dto.CreateOrderRequest;
import com.pharmacy.onlinepharmacy.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> createOrder(Authentication auth, @RequestBody CreateOrderRequest request) {
        Long orderId = orderService.createOrder(auth.getName(), request);
        return ResponseEntity.ok(orderId);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id, Authentication auth) {
        orderService.cancelOrder(id, auth.getName());
        return ResponseEntity.ok("Заказ отменен");
    }
}