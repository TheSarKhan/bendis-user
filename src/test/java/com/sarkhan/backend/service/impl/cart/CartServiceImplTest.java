package com.sarkhan.backend.service.impl.cart;

import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.cart.UserCartDTO;
import com.sarkhan.backend.exception.NotEnoughQuantityException;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.cart.Cart;
import com.sarkhan.backend.model.cart.CartItem;
import com.sarkhan.backend.model.enums.Color;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.ColorAndSize;
import com.sarkhan.backend.repository.cart.CartItemRepository;
import com.sarkhan.backend.repository.cart.CartRepository;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock private CartRepository cartRepository;
    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private Product product;
    private ColorAndSize colorAndSize;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(new ArrayList<>());

        product = new Product();
        product.setId(1L);
        product.setDiscountedPrice(BigDecimal.valueOf(50));

        colorAndSize = new ColorAndSize();
        colorAndSize.setColor(Color.BLACK);
        colorAndSize.setStock(1L);
        colorAndSize.setSizeStockMap(new HashMap<>(Map.of("M", 1L)));

        product.setColorAndSizes(List.of(colorAndSize));
    }

    @Test
    void testAddToCart_Success_WithSize() throws NotEnoughQuantityException {
        CartItemRequestDTO dto = new CartItemRequestDTO(1L, 1L,2,"BLACK", "M", BigDecimal.valueOf(100));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProductIdAndColor(any(), anyLong(), anyString()))
                .thenReturn(Optional.empty());

        cartService.addToCart(1L, dto);

        verify(cartItemRepository).save(any(CartItem.class));
        verify(productRepository).save(product);
    }

    @Test
    void testAddToCart_NotEnoughSizeStock() {
        CartItemRequestDTO dto = new CartItemRequestDTO(1L, 1L,3,"BLACK", "M", BigDecimal.valueOf(1));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(NotEnoughQuantityException.class, () -> cartService.addToCart(1L, dto));
    }

    @Test
    void testGetUserCart_Success() {
        CartItem item = CartItem.builder().productId(1L).color("BLACK").quantity(2).build();
        cart.setCartItems(List.of(item));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        UserCartDTO result = cartService.getUserCart(1L);

        assertEquals(1, result.getItems().size());
        assertTrue(result.getTotalPrice() > 0);
    }

    @Test
    void testUpdateCartItem_Success() {
        CartItem cartItem = CartItem.builder().productId(1L).color("BLACK").quantity(2).build();
        CartItemRequestDTO dto = new CartItemRequestDTO(1L, 1L,2,"BLACK", "M", BigDecimal.valueOf(100));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartAndProductIdAndColor(any(), anyLong(), anyString())).thenReturn(Optional.of(cartItem));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        cartService.updateCartItem(1L, dto);

        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void testRemoveCartItem_Success() {
        CartItem cartItem = CartItem.builder().productId(1L).color("BLACK").quantity(2).build();
        cart.setCartItems(List.of(cartItem));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartAndProductIdAndColor(cart, 1L, "BLACK"))
                .thenReturn(Optional.of(cartItem));

        cartService.removeCartItem(1L, 1L, "BLACK");

        verify(cartItemRepository).delete(cartItem);
    }
}
