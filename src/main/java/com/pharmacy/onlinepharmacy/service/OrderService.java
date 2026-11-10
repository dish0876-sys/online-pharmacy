package com.pharmacy.onlinepharmacy.service;

import com.pharmacy.onlinepharmacy.dto.CreateOrderRequest;
import com.pharmacy.onlinepharmacy.entity.*;
import com.pharmacy.onlinepharmacy.enums.OrderStatus;
import com.pharmacy.onlinepharmacy.enums.PrescriptionStatus;
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
    private final PrescriptionRepository prescriptionRepository;

    @Transactional
    public Long createOrder(String email, CreateOrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<CartItem> cartItems = cartItemRepository.findAllByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Корзина пуста");
        }

        // Проверка рецепта
        boolean hasPrescriptionItems = cartItems.stream()
                .anyMatch(item -> item.getProduct().isPrescriptionRequired());

        if (hasPrescriptionItems) {
            if (request.getPrescriptionId() == null) {
                throw new RuntimeException("В корзине есть рецептурные товары. Необходимо передать ID рецепта.");
            }

            Prescription prescription = prescriptionRepository.findById(request.getPrescriptionId())
                    .orElseThrow(() -> new RuntimeException("Рецепт с таким ID не найден"));

            if (!prescription.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Этот рецепт принадлежит другому пользователю");
            }

            if (prescription.getStatus() != PrescriptionStatus.APPROVED) {
                throw new RuntimeException("Рецепт не одобрен фармацевтом или уже был использован");
            }

            prescription.setStatus(PrescriptionStatus.USED);
            prescriptionRepository.save(prescription);
        }

        // Создаем сам заказ
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

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Недостаточно товара на складе: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

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

        cartItemRepository.deleteAllByUser(user);

        return order.getId();
    }

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

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case CREATED -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.ASSEMBLING || next == OrderStatus.CANCELLED;
            case ASSEMBLING -> next == OrderStatus.SHIPPED;
            case SHIPPED -> next == OrderStatus.COMPLETED;
            default -> false;
        };
    }

    @Transactional
    public void cancelOrder(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("У вас нет прав на отмену этого заказа");
        }

        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Заказ уже нельзя отменить");
        }

        for (var item : order.getItems()) {
            var product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}