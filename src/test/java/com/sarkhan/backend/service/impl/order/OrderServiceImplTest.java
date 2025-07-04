package com.sarkhan.backend.service.impl.order;

import com.sarkhan.backend.dto.address.AddressRequestDto;
import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.exception.NotEnoughQuantityException;
import com.sarkhan.backend.handler.exception.ResourceNotFoundException;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.cart.Cart;
import com.sarkhan.backend.model.cart.CartItem;
import com.sarkhan.backend.model.enums.Color;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.ColorAndSize;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.payment.service.PaymentService;
import com.sarkhan.backend.repository.cart.CartItemRepository;
import com.sarkhan.backend.repository.cart.CartRepository;
import com.sarkhan.backend.repository.order.AddressRepository;
import com.sarkhan.backend.repository.order.OrderItemRepository;
import com.sarkhan.backend.repository.order.OrderRepository;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;
    private ColorAndSize colorAndSize;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        cartItem = CartItem.builder()
                .productId(1L)
                .color("RED")
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(20))
                .build();

        cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(List.of(cartItem));

        colorAndSize = new ColorAndSize();
        colorAndSize.setColor(Color.RED);
        colorAndSize.setStock(10L);
        colorAndSize.setSizeStockMap(new HashMap<>());

        product = new Product();
        product.setId(1L);
        product.setColorAndSizes(List.of(colorAndSize));
        product.setDiscountedPrice(BigDecimal.TEN);

        AddressRequestDto addressDto = new AddressRequestDto(
                "FIN123", "Baku", "Narimanov", "Main street", "AZ1000"
        );

        CartItemRequestDTO cartItemRequestDTO = new CartItemRequestDTO(
                1L, 1L, 2, "RED", null, BigDecimal.TEN
        );

        orderRequest = new OrderRequest(1L, List.of(cartItemRequestDTO), 100.0, addressDto);
    }

    @Test
    void testCreateOrder_Success() throws NotEnoughQuantityException {
        when(jwtService.extractEmail(any())).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(paymentService.createInvoice(any(), any())).thenReturn("invoice_url");

        String result = orderService.createOrder(orderRequest, "Bearer token");

        assertNotNull(result);
        assertEquals("invoice_url", result);
        verify(orderRepository).save(any());
        verify(orderItemRepository).saveAll(any());
        verify(cartItemRepository).deleteAll(cart.getCartItems());
    }


    @Test
    void testCreateOrder_UserNotFound() {
        when(jwtService.extractEmail(any())).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder(orderRequest, "Bearer token"));
    }

    @Test
    void testCreateOrder_NotEnoughStock() {
        colorAndSize.setStock(1L);
        product.setColorAndSizes(List.of(colorAndSize));

        when(jwtService.extractEmail(any())).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(NotEnoughQuantityException.class,
                () -> orderService.createOrder(orderRequest, "Bearer token"));
    }



    @Test
    void testCreateOrder_ProductNotFound() {
        when(jwtService.extractEmail(any())).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder(orderRequest, "Bearer token"));
    }

}