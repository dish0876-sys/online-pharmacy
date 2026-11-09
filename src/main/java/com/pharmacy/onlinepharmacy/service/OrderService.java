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
}