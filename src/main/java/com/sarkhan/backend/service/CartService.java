package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.cart.UserCartDTO;

import java.util.List;

public interface CartService {
    void addToCart(Long userId, CartItemRequestDTO basketItemDTO);

    UserCartDTO getUserCart(Long userId);

    void updateCartItem(Long userId, CartItemRequestDTO cartItemDTO);

   // List<CartItemRequestDTO> getUserBasket(Long userId);
}
