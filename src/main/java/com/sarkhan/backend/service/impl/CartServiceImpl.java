package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.cart.CartItemResponseDTO;
import com.sarkhan.backend.dto.cart.UserCartDTO;
import com.sarkhan.backend.handler.exception.NotEnoughQuantityException;
import com.sarkhan.backend.handler.exception.ResourceNotFoundException;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.cart.Cart;
import com.sarkhan.backend.model.cart.CartItem;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.ColorAndSize;
import com.sarkhan.backend.repository.cart.CartItemRepository;
import com.sarkhan.backend.repository.cart.CartRepository;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void addToCart(Long userId, CartItemRequestDTO cartItemRequestDTO) throws NotEnoughQuantityException {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .build();
                    return cartRepository.save(newCart);
                });
        Product product = productRepository.findById(cartItemRequestDTO.getProductId())
                .orElseThrow(() -> {
                    log.error("Product can not found");
                    return new ResourceNotFoundException("Product can not found");
                });
        ColorAndSize color = product.getColorAndSizes().stream()
                .filter(c -> c.getColor().name().equalsIgnoreCase(cartItemRequestDTO.getColor()))
                .findFirst().orElseThrow(() -> {
                    log.error("Color can not found" + cartItemRequestDTO.getColor());
                    return new ResourceNotFoundException("Product can not found");
                });
        if (cartItemRequestDTO.getSize() != null && !cartItemRequestDTO.getSize().isBlank()) {
            Long stock = color.getSizeStockMap().get(cartItemRequestDTO.getSize());
            if (stock == null || stock < cartItemRequestDTO.getQuantity()) {
                throw new NotEnoughQuantityException("Not enough quantity for this product " + cartItemRequestDTO.getQuantity());
            }
            color.getSizeStockMap().put(cartItemRequestDTO.getSize(),
                    stock - cartItemRequestDTO.getQuantity());
        } else {
            if (color.getStock() < cartItemRequestDTO.getQuantity()) {
                throw new NotEnoughQuantityException("Not enough quantity for this product " + cartItemRequestDTO.getQuantity());
            }
            color.setStock(color.getStock() - cartItemRequestDTO.getQuantity());
        }

        CartItem existingItem = cartItemRepository.findByCartAndProductIdAndColor(
                cart, cartItemRequestDTO.getProductId(), cartItemRequestDTO.getColor()
        ).orElse(null);

        int quantity = cartItemRequestDTO.getQuantity();
        BigDecimal totalPrice = product.getDiscountedPrice().multiply(BigDecimal.valueOf(quantity));

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + cartItemRequestDTO.getQuantity();
            existingItem.setQuantity(newQuantity);
            BigDecimal newPrice = product.getDiscountedPrice().multiply(BigDecimal.valueOf(newQuantity));
            existingItem.setTotalPrice(newPrice);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(product.getId())
                    .color(cartItemRequestDTO.getColor())
                    .quantity(cartItemRequestDTO.getQuantity())
                    .totalPrice(totalPrice)
                    .totalPrice(totalPrice)
                    .build();

            cartItemRepository.save(newItem);
        }
        productRepository.save(product);

    }

    @Override
    public UserCartDTO getUserCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("Cart can not found");
            return new ResourceNotFoundException("Cart can not found");
        });

        List<CartItem> cartItems = cart.getCartItems();
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> productRepository.findById(item.getProductId())
                        .map(p -> Double.parseDouble(p.getDiscountedPrice().toString()) * item.getQuantity())
                        .orElse(0.0))
                .sum();

        return UserCartDTO.builder()
                .items(mapToResponseDTOList(cartItems))
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public void updateCartItem(Long userId, CartItemRequestDTO cartItemDTO) {

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("Cart can not found");
            return new ResourceNotFoundException("Cart can not found");
        });

        CartItem cartItem = cartItemRepository.findByCartAndProductIdAndColor(
                cart, cartItemDTO.getProductId(), cartItemDTO.getColor()
        ).orElseThrow(() -> {
            log.error("Cart item not found");
            return new ResourceNotFoundException("Cart item not found");
        });

        Product product = productRepository.findById(cartItemDTO.getProductId()).orElseThrow(() -> {
            log.error("Product not found");
            return new ResourceNotFoundException("Product not found");
        });

        cartItem.setQuantity(cartItemDTO.getQuantity());
        BigDecimal newTotal = product.getDiscountedPrice().multiply(BigDecimal.valueOf(cartItemDTO.getQuantity()));
        cartItem.setTotalPrice(newTotal);

        cartItemRepository.save(cartItem);
    }

    @Override
    public Long extractUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email).orElseThrow().getId();
    }

    @Override
    public void removeCartItem(Long userId, Long productId, String color) {

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("Cart can not found");
            return new ResourceNotFoundException("Cart can not found");
        });

        CartItem cartItem = cartItemRepository.findByCartAndProductIdAndColor(cart, productId, color)
                .orElseThrow(() -> {
                    log.error("Cart item not found");
                    return new ResourceNotFoundException("Cart item not found");
                });

        cartItemRepository.delete(cartItem);
    }

    private List<CartItemResponseDTO> mapToResponseDTOList(List<CartItem> items) {
        List<CartItemResponseDTO> dtos = new ArrayList<>();
        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            dtos.add(CartItemResponseDTO.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .color(item.getColor())
                    .totalPrice(product.getDiscountedPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build());
        }
        return dtos;
    }
}