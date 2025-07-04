package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.cart.UserCartDTO;
import com.sarkhan.backend.exception.NotEnoughQuantityException;
public interface CartService {
    void addToCart(Long userId, CartItemRequestDTO basketItemDTO) throws NotEnoughQuantityException;

    UserCartDTO getUserCart(Long userId);

    void updateCartItem(Long userId, CartItemRequestDTO cartItemDTO);
    Long extractUserIdFromToken(String authHeader);
    void removeCartItem(Long userId, Long productId, String color);
   // List<CartItemRequestDTO> getUserBasket(Long userId);
}
