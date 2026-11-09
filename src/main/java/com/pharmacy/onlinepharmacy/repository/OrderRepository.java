package com.pharmacy.onlinepharmacy.repository;

import com.pharmacy.onlinepharmacy.entity.Order;
import com.pharmacy.onlinepharmacy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Найти все заказы пользователя, отсортированные от новых к старым
    List<Order> findAllByUserOrderByCreatedAtDesc(User user);
}