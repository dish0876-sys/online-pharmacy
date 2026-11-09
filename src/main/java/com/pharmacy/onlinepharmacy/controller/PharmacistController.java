package com.pharmacy.onlinepharmacy.controller;

import com.pharmacy.onlinepharmacy.entity.Order;
import com.pharmacy.onlinepharmacy.entity.Prescription;
import com.pharmacy.onlinepharmacy.enums.OrderStatus;
import com.pharmacy.onlinepharmacy.enums.PrescriptionStatus;
import com.pharmacy.onlinepharmacy.repository.OrderRepository;
import com.pharmacy.onlinepharmacy.repository.PrescriptionRepository;
import com.pharmacy.onlinepharmacy.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pharmacist") // Базовый путь теперь /pharmacist (как в ТЗ)
@RequiredArgsConstructor
public class PharmacistController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PrescriptionRepository prescriptionRepository;

    // ==================== ЗАКАЗЫ ====================

    // GET /pharmacist/orders
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrdersForAssembly() {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
                .toList();
        return ResponseEntity.ok(orders);
    }

    // PUT /pharmacist/orders/{id}/assemble
    @PutMapping("/orders/{id}/assemble")
    public ResponseEntity<String> assembleOrder(@PathVariable Long id) {
        orderService.updateOrderStatus(id, OrderStatus.ASSEMBLING);
        return ResponseEntity.ok("Заказ переведен в сборку");
    }

    // PUT /pharmacist/orders/{id}/ship
    @PutMapping("/orders/{id}/ship")
    public ResponseEntity<String> shipOrder(@PathVariable Long id) {
        orderService.updateOrderStatus(id, OrderStatus.SHIPPED);
        return ResponseEntity.ok("Заказ передан в доставку");
    }

    // ==================== РЕЦЕПТЫ ====================

    // GET /pharmacist/prescriptions
    @GetMapping("/prescriptions")
    public ResponseEntity<List<Prescription>> getPendingPrescriptions() {
        return ResponseEntity.ok(prescriptionRepository.findAllByStatus(PrescriptionStatus.PENDING));
    }

    // PUT /pharmacist/prescriptions/{id}/approve
    @PutMapping("/prescriptions/{id}/approve")
    public ResponseEntity<String> approvePrescription(@PathVariable Long id) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Рецепт не найден")); // Исправлено orElseThrow
        p.setStatus(PrescriptionStatus.APPROVED);
        prescriptionRepository.save(p);
        return ResponseEntity.ok("Рецепт одобрен");
    }

    // PUT /pharmacist/prescriptions/{id}/reject
    @PutMapping("/prescriptions/{id}/reject")
    public ResponseEntity<String> rejectPrescription(@PathVariable Long id, @RequestParam String reason) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Рецепт не найден")); // Исправлено orElseThrow
        p.setStatus(PrescriptionStatus.REJECTED);
        p.setRejectionReason(reason);
        prescriptionRepository.save(p);
        return ResponseEntity.ok("Рецепт отклонен");
    }
}