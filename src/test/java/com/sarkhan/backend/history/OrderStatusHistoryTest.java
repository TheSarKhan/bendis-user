package com.sarkhan.backend.history;

import com.sarkhan.backend.dto.history.OrderHistoryResponseDto;
import com.sarkhan.backend.handler.exception.DataNotFoundException;
import com.sarkhan.backend.model.enums.Color;
import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.model.order.OrderItem;
import com.sarkhan.backend.model.order.OrderStatusHistory;
import com.sarkhan.backend.model.order.PaymentHistory;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.ColorAndSize;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.order.OrderRepository;
import com.sarkhan.backend.repository.order.OrderStatusHistoryRepository;
import com.sarkhan.backend.repository.order.PaymentHistoryRepository;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.impl.OrderStatusHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OrderStatusHistoryTest {

    @InjectMocks
    private OrderStatusHistoryServiceImpl orderStatusHistoryService;
    @Mock
    private OrderStatusHistoryRepository orderStatusHistoryRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;
    @Mock
    private UserRepository userRepository;

    private User user;
    private OrderStatusHistory orderStatusHistory;
    private Order order;
    private OrderItem orderItem;
    private Product product;
    private ColorAndSize colorAndSize;
    private PaymentHistory paymentHistory;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        order = new Order();
        order.setOrderId(1L);
        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProductId(1L);
        order.setOrderItemList(List.of(orderItem));
        colorAndSize = new ColorAndSize();
        colorAndSize.setColor(Color.RED);
        colorAndSize.setStock(10L);
        colorAndSize.setSizeStockMap(new HashMap<>());

        product = new Product();
        product.setId(1L);
        product.setColorAndSizes(List.of(colorAndSize));
        product.setDiscountedPrice(BigDecimal.TEN);
        product.setSalesCount(0);
        product.setOriginalPrice(BigDecimal.valueOf(100));
        product.setDiscountedPrice(BigDecimal.valueOf(50));
        product.setColorAndSizes(List.of(ColorAndSize.builder().color(Color.BLUE).imageUrls(List.of("imageUrl")).build()));
        orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrderStatus(OrderStatus.SHIPPED);
        paymentHistory = new PaymentHistory();
        paymentHistory.setCardLastFourDigits("1234");
    }


    @Test
    void testGetOrderHistory_ShouldReturnOrderHistoryResponseDto() throws DataNotFoundException {
        String email = "user@example.com";
        Long userId = 1L;
        Long orderId = 1L;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(orderRepository.findByUserId(userId)).thenReturn((List.of(order)));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderStatusHistoryRepository.findLastByOrder_OrderIdOrderByChangedAtDesc(orderId)).thenReturn(Optional.ofNullable(orderStatusHistory));
        when(paymentHistoryRepository.findLastPaymentByOrder_OrderIdOrderByPaidDateDesc(orderId)).thenReturn(Optional.ofNullable(paymentHistory));

        List<OrderHistoryResponseDto> orderHistory = orderStatusHistoryService.getOrderHistory(email);
        assertEquals(1, orderHistory.size());
        OrderHistoryResponseDto orderHistoryResponseDto = orderHistory.getFirst();
        assertEquals(orderId, orderHistoryResponseDto.getOrderId());
        assertEquals(OrderStatus.SHIPPED, orderHistoryResponseDto.getOrderStatus());
        assertEquals("1234", orderHistoryResponseDto.getCardLast4Number());
    }

    @Test
    void testGetOrderHistory_WhenUserCanNotFound() {
        String email = "notFound";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class,()-> orderStatusHistoryService.getOrderHistory(email));
    }

    @Test
    void testGetOrderHistory_WhenProductCanNotFound() {
        String email = "user@example.com";
        Long userId = 1L;
        Long productId = 1L;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(orderRepository.findByUserId(userId)).thenReturn((List.of(order)));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,()-> orderStatusHistoryService.getOrderHistory(email));

    }
}
