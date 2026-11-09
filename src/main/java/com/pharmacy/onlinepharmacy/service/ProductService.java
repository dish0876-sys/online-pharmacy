package com.pharmacy.onlinepharmacy.service;

import com.pharmacy.onlinepharmacy.dto.CreateProductRequest;
import com.pharmacy.onlinepharmacy.dto.ProductResponse;
import com.pharmacy.onlinepharmacy.entity.Product;
import com.pharmacy.onlinepharmacy.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> getAllProducts() {
        // Берем только те, что не удалены (isDeleted = false)
        return productRepository.findAllByIsDeletedFalse().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .prescriptionRequired(request.isPrescriptionRequired())
                .stockQuantity(request.getStockQuantity())
                .isDeleted(false)
                .build();

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .prescriptionRequired(product.isPrescriptionRequired())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));
        return mapToResponse(product);
    }
}