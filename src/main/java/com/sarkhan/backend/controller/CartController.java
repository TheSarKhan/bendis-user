package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.cart.UserCartDTO;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private Long extractUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7); // "Bearer "
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email).orElseThrow().getId();
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody CartItemRequestDTO cartItemRequestDTO) {
        Long userId = extractUserIdFromToken(authHeader);
        cartService.addToCart(userId, cartItemRequestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/view")
    public ResponseEntity<UserCartDTO> getCart(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        return ResponseEntity.ok(cartService.getUserCart(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateCartItem(@RequestHeader("Authorization") String authHeader,
                                               @RequestBody CartItemRequestDTO cartItemDTO) {
        Long userId = extractUserIdFromToken(authHeader);
        cartService.updateCartItem(userId, cartItemDTO);
        return ResponseEntity.ok().build();
    }
}
