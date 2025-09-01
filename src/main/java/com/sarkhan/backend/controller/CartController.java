package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.cart.UserCartDTO;
import com.sarkhan.backend.handler.exception.NotEnoughQuantityException;
import com.sarkhan.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addToCart(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody CartItemRequestDTO cartItemRequestDTO) throws NotEnoughQuantityException {
        Long userId = cartService.extractUserIdFromToken(authHeader);
        cartService.addToCart(userId, cartItemRequestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<UserCartDTO> getCart(@RequestHeader("Authorization") String authHeader) {
        Long userId = cartService.extractUserIdFromToken(authHeader);
        return ResponseEntity.ok(cartService.getUserCart(userId));
    }

    @PutMapping
    public ResponseEntity<Void> updateCartItem(@RequestHeader("Authorization") String authHeader,
                                               @RequestBody CartItemRequestDTO cartItemDTO) {
        Long userId = cartService.extractUserIdFromToken(authHeader);
        cartService.updateCartItem(userId, cartItemDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long userId, @RequestParam Long productId, @RequestParam String color) {
        cartService.removeCartItem(userId, productId, color);
        return ResponseEntity.ok().build();
    }
}
