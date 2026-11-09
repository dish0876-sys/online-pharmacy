package com.pharmacy.onlinepharmacy.controller;

import com.pharmacy.onlinepharmacy.entity.Order;
import com.pharmacy.onlinepharmacy.enums.OrderStatus;
import com.pharmacy.onlinepharmacy.repository.OrderRepository;
import com.pharmacy.onlinepharmacy.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pharmacist/orders")
@RequiredArgsConstructor
public class PharmacistController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    // Получить заказы, ожидающие сборки (статус CONFIRMED)
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersForAssembly() {
        // В реальном проекте лучше использовать кастомный запрос в репозитории
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
                .toList();
        return ResponseEntity.ok(orders);
    }

    // Подтвердить сборку (confirmed -> assembling)
    @PutMapping("/{id}/assemble")
    public ResponseEntity<String> assembleOrder(@PathVariable Long id) {
        orderService.updateOrderStatus(id, OrderStatus.ASSEMBLING);
        return ResponseEntity.ok("Заказ переведен в сборку");
    }

    // Передать в доставку (assembling -> shipped)
    @PutMapping("/{id}/ship")
    public ResponseEntity<String> shipOrder(@PathVariable Long id) {
        orderService.updateOrderStatus(id, OrderStatus.SHIPPED);
        return ResponseEntity.ok("Заказ передан в доставку");
    }
}