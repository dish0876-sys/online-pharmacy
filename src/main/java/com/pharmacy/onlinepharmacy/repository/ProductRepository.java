package com.pharmacy.onlinepharmacy.repository;

import com.pharmacy.onlinepharmacy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Пример кастомного метода: найти все неудаленные товары
    List<Product> findAllByIsDeletedFalse();
}