package com.pharmacy.onlinepharmacy.controller;

import com.pharmacy.onlinepharmacy.dto.CartItemRequest;
import com.pharmacy.onlinepharmacy.dto.CartResponse;
import com.pharmacy.onlinepharmacy.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication auth) {
        return ResponseEntity.ok(cartService.getCart(auth.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<String> addItem(Authentication auth, @RequestBody CartItemRequest request) {
        cartService.addToCart(auth.getName(), request);
        return ResponseEntity.ok("Товар добавлен в корзину");
    }

    @DeleteMapping
    public ResponseEntity<String> clearCart(Authentication auth) {
        cartService.clearCart(auth.getName());
        return ResponseEntity.ok("Корзина очищена");
    }
}