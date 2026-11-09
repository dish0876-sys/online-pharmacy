package com.pharmacy.onlinepharmacy.service;

import com.pharmacy.onlinepharmacy.dto.*;
import com.pharmacy.onlinepharmacy.entity.CartItem;
import com.pharmacy.onlinepharmacy.entity.Product;
import com.pharmacy.onlinepharmacy.entity.User;
import com.pharmacy.onlinepharmacy.repository.CartItemRepository;
import com.pharmacy.onlinepharmacy.repository.ProductRepository;
import com.pharmacy.onlinepharmacy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Получить корзину текущего пользователя
    public CartResponse getCart(String email) {
        User user = getUser(email);
        List<CartItem> items = cartItemRepository.findAllByUser(user);

        List<CartItemDto> dtos = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : items) {
            BigDecimal subtotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            dtos.add(CartItemDto.builder()
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .price(item.getProduct().getPrice())
                    .subtotal(subtotal)
                    .build());
            total = total.add(subtotal);
        }
        return new CartResponse(dtos, total);
    }

    // Добавить товар в корзину
    @Transactional
    public void addToCart(String email, CartItemRequest request) {
        User user = getUser(email);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        if (product.isDeleted() || product.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Недостаточно товара на складе или товар удален");
        }

        // Если товар уже в корзине, увеличиваем количество
        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product)
                .orElse(CartItem.builder().user(user).product(product).quantity(0).build());

        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItemRepository.save(cartItem);
    }

    // Очистить корзину
    @Transactional
    public void clearCart(String email) {
        cartItemRepository.deleteAllByUser(getUser(email));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
}