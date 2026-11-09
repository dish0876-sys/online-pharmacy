package com.pharmacy.onlinepharmacy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price; // Для денег используем BigDecimal, а не Double

    @Column(name = "prescription_required", nullable = false)
    private boolean prescriptionRequired = false;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    // Soft delete: товар не удаляется из БД, а помечается как удаленный
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}