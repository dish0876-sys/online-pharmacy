package com.pharmacy.onlinepharmacy.service;

import com.pharmacy.onlinepharmacy.dto.CreateOrderRequest;
import com.pharmacy.onlinepharmacy.entity.*;
import com.pharmacy.onlinepharmacy.enums.OrderStatus;
import com.pharmacy.onlinepharmacy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createOrder(String email, CreateOrderRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow();
        List<CartItem> cartItems = cartItemRepository.findAllByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Корзина пуста");
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .totalPrice(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // Проверяем остатки перед созданием заказа
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Недостаточно товара: " + product.getName());
            }

            // Списываем остаток
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Создаем позицию заказа
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();

            order.getItems().add(orderItem);
            totalPrice = totalPrice.add(orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }

        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        // Очищаем корзину
        cartItemRepository.deleteAllByUser(user);

        return order.getId();
    }

    // Метод смены статуса
    @Transactional
    public void cancelOrder(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        // Проверяем, что заказ принадлежит этому пользователю
        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("У вас нет прав на отмену этого заказа");
        }

        // Отменить можно только в CREATED или CONFIRMED
        if (order.getStatus() != com.pharmacy.onlinepharmacy.enums.OrderStatus.CREATED &&
                order.getStatus() != com.pharmacy.onlinepharmacy.enums.OrderStatus.CONFIRMED) {
            throw new RuntimeException("Заказ уже нельзя отменить");
        }

        // Возвращаем товары на склад!
        for (var item : order.getItems()) {
            var product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }

        order.setStatus(com.pharmacy.onlinepharmacy.enums.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // Метод для смены статуса заказа
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (!isValidTransition(order.getStatus(), newStatus)) {
            throw new RuntimeException("Недопустимый переход статуса: " + order.getStatus() + " -> " + newStatus);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    // Проверка цепочки статусов
    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case CREATED -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.ASSEMBLING || next == OrderStatus.CANCELLED;
            case ASSEMBLING -> next == OrderStatus.SHIPPED;
            case SHIPPED -> next == OrderStatus.COMPLETED;
            default -> false;
        };
    }
}