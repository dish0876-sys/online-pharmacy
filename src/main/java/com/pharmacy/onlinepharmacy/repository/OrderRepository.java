package com.pharmacy.onlinepharmacy.repository;

import com.pharmacy.onlinepharmacy.entity.Order;
import com.pharmacy.onlinepharmacy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Найти все заказы пользователя, отсортированные от новых к старым
    List<Order> findAllByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status != 'CANCELLED' AND o.createdAt BETWEEN :from AND :to")
    BigDecimal sumRevenueBetweenDates(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status != 'CANCELLED' AND o.createdAt BETWEEN :from AND :to")
    Long countOrdersBetweenDates(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}