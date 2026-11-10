package com.pharmacy.onlinepharmacy.controller;

import com.pharmacy.onlinepharmacy.dto.CreateProductRequest;
import com.pharmacy.onlinepharmacy.dto.ProductResponse;
import com.pharmacy.onlinepharmacy.dto.SalesReportResponse;
import com.pharmacy.onlinepharmacy.dto.TopProductResponse;
import com.pharmacy.onlinepharmacy.dto.UpdateProductRequest;
import com.pharmacy.onlinepharmacy.entity.Product;
import com.pharmacy.onlinepharmacy.repository.ProductRepository;
import com.pharmacy.onlinepharmacy.service.ProductService;
import com.pharmacy.onlinepharmacy.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final ReportService reportService;             // Добавили сервис отчетов
    private final ProductRepository productRepository;     // Добавили репозиторий товаров (для отчета по остаткам)

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Товар успешно удален (soft delete)");
    }

    @PutMapping("/stock/{productId}")
    public ResponseEntity<String> updateStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        productService.updateStock(productId, quantity);
        return ResponseEntity.ok("Остаток на складе обновлен");
    }

    // 1. Отчет по продажам (выручка и количество заказов за период)
    // Пример запроса: GET /admin/reports/sales?from=2023-01-01T00:00:00&to=2023-12-31T23:59:59
    @GetMapping("/reports/sales")
    public ResponseEntity<SalesReportResponse> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        return ResponseEntity.ok(reportService.getSalesReport(from, to));
    }

    // 2. Топ популярных товаров
    // Пример запроса: GET /admin/reports/top-products?limit=3
    @GetMapping("/reports/top-products")
    public ResponseEntity<List<TopProductResponse>> getTopProducts(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reportService.getTopProducts(limit));
    }

    // 3. Отчет по остаткам (товары, которых осталось мало)
    // Пример запроса: GET /admin/reports/low-stock?threshold=10
    @GetMapping("/reports/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold) {
        List<Product> lowStock = productRepository.findAllByIsDeletedFalse().stream()
                .filter(p -> p.getStockQuantity() <= threshold)
                .toList();
        return ResponseEntity.ok(lowStock);
    }
}