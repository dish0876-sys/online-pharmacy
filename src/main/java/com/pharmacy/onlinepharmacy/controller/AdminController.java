package com.pharmacy.onlinepharmacy.controller;

import com.pharmacy.onlinepharmacy.dto.CreateProductRequest;
import com.pharmacy.onlinepharmacy.dto.ProductResponse;
import com.pharmacy.onlinepharmacy.dto.UpdateProductRequest;
import com.pharmacy.onlinepharmacy.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;

    // ==================== УПРАВЛЕНИЕ ТОВАРАМИ ====================

    // POST /admin/products
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    // PUT /admin/products/{id}
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // DELETE /admin/products/{id}
    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Товар успешно удален (soft delete)");
    }

    // PUT /admin/stock/{productId}
    @PutMapping("/stock/{productId}")
    public ResponseEntity<String> updateStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        productService.updateStock(productId, quantity);
        return ResponseEntity.ok("Остаток на складе обновлен");
    }
}