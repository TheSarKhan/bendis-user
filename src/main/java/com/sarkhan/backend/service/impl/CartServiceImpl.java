package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.cart.CartItemResponseDTO;
import com.sarkhan.backend.dto.cart.UserCartDTO;
import com.sarkhan.backend.model.cart.CartItem;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.repository.cart.CartItemRepository;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    @Override
    public void addToCart(Long userId, CartItemRequestDTO basketItemDTO) {
        Product product = productRepository.findById(basketItemDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem newItem = CartItem.builder()
                .userId(userId)
                .productId(product.getId())
                .quantity(basketItemDTO.getQuantity())
                .build();

        cartItemRepository.save(newItem);
    }

    public UserCartDTO getUserCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        double totalPrice = items.stream()
                .mapToDouble(item -> productRepository.findById(item.getProductId())
                        .map(p -> p.getPrice() * item.getQuantity())
                        .orElse(0.0))
                .sum();
        return UserCartDTO.builder()
                .items(mapToResponseDTOList(items))
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public void updateCartItem(Long userId, CartItemRequestDTO cartItemDTO) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, cartItemDTO.getProductId());
        if (cartItem == null) {
            throw new RuntimeException("Cart item not found");
        }
        cartItem.setQuantity(cartItemDTO.getQuantity());
        cartItemRepository.save(cartItem);
    }

    private List<CartItemResponseDTO> mapToResponseDTOList(List<CartItem> items) {
        List<CartItemResponseDTO> dtos = new ArrayList<>();
        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            dtos.add(CartItemResponseDTO.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .totalPrice(product.getPrice() * item.getQuantity())
                    .build());
        }
        return dtos;
    }
}