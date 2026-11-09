package com.pharmacy.onlinepharmacy.repository;

import com.pharmacy.onlinepharmacy.entity.CartItem;
import com.pharmacy.onlinepharmacy.entity.User;
import com.pharmacy.onlinepharmacy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Найти все товары в корзине конкретного пользователя
    List<CartItem> findAllByUser(User user);

    // Найти конкретную позицию в корзине (по пользователю и товару)
    Optional<CartItem> findByUserAndProduct(User user, Product product);

    // Удалить всё из корзины пользователя (нужно для очистки после заказа)
    void deleteAllByUser(User user);
}