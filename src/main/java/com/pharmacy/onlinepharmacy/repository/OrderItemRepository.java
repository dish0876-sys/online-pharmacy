package com.pharmacy.onlinepharmacy.repository;

import com.pharmacy.onlinepharmacy.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi.product.name as productName, SUM(oi.quantity) as totalSold " +
            "FROM OrderItem oi JOIN oi.order o " +
            "WHERE o.status != 'CANCELLED' " +
            "GROUP BY oi.product.id, oi.product.name " +
            "ORDER BY totalSold DESC")
    List<Object[]> findTopProducts(); // Возвращает список массивов
}