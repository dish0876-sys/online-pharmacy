package com.pharmacy.onlinepharmacy.controller;

import com.pharmacy.onlinepharmacy.dto.CreateProductRequest;
import com.pharmacy.onlinepharmacy.dto.ProductResponse;
import com.pharmacy.onlinepharmacy.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Эндпоинт для получения списка всех товаров (доступен всем)
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ВРЕМЕННЫЙ эндпоинт для создания товара (позже перенесем в админку)
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }
}