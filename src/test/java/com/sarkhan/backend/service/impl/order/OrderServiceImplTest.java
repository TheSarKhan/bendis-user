package com.sarkhan.backend.service.impl.order;

import com.sarkhan.backend.dto.address.AddressRequestDto;
import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.order.OrderDetailsDto;
import com.sarkhan.backend.dto.order.OrderFilterRequest;
import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.dto.order.OrderResponseDto;
import com.sarkhan.backend.exception.NotEnoughQuantityException;
import com.sarkhan.backend.handler.exception.ResourceNotFoundException;
import com.sarkhan.backend.mapper.order.OrderMapper;
import com.sarkhan.backend.model.cart.Cart;
import com.sarkhan.backend.model.cart.CartItem;
import com.sarkhan.backend.model.enums.Color;
import com.sarkhan.backend.model.enums.DateType;
import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.Address;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.model.order.OrderItem;
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
import com.sarkhan.backend.service.UserService;
import com.sarkhan.backend.service.impl.OrderServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
    private UserService userService;

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
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private EntityManager entityManager;
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
                .color("BLUE")
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(20))
                .build();

        cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(List.of(cartItem));

        colorAndSize = new ColorAndSize();
        colorAndSize.setColor(Color.BLUE);
        colorAndSize.setStock(10L);
        colorAndSize.setSizeStockMap(new HashMap<>());

        product = new Product();
        product.setId(1L);
        product.setColorAndSizes(List.of(colorAndSize));
        product.setDiscountedPrice(BigDecimal.TEN);
        product.setSalesCount(0);
        product.setOriginalPrice(BigDecimal.valueOf(100));
        product.setDiscountedPrice(BigDecimal.valueOf(50));
        product.setColorAndSizes(List.of(ColorAndSize.builder().color(Color.BLUE).stock(2L).imageUrls(List.of("imageUrl")).build()));
        AddressRequestDto addressDto = new AddressRequestDto(
                "FIN123", "Baku", "Narimanov", "Main street", "AZ1000"
        );

        CartItemRequestDTO cartItemRequestDTO = new CartItemRequestDTO(
                1L, 2, "BLUE", null, BigDecimal.TEN
        );

        orderRequest = new OrderRequest(List.of(cartItemRequestDTO), 100.0, addressDto);
    }

    @Test
    void testCreateOrder_Success() throws NotEnoughQuantityException {
        securityContextHolderConfigCorrectly();
        when(userService.getByEmail("user@example.com")).thenReturn(user);
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(paymentService.createInvoice(any(), any(User.class))).thenReturn("invoice_url");

        String result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals("invoice_url", result);
        verify(orderRepository).save(any());
        verify(orderItemRepository).saveAll(any());
        verify(cartItemRepository).deleteAll(cart.getCartItems());
    }

    @Test
    void testCreateOrder_NotEnoughStock() {
        colorAndSize.setStock(1L);
        product.setColorAndSizes(List.of(colorAndSize));

        securityContextHolderConfigCorrectly();
        when(userService.getByEmail("user@example.com")).thenReturn(user);
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(NotEnoughQuantityException.class,
                () -> orderService.createOrder(orderRequest));
    }


    @Test
    void testCreateOrder_ProductNotFound() {
        securityContextHolderConfigCorrectly();
        when(userService.getByEmail("user@example.com")).thenReturn(user);
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder(orderRequest));
    }

    @Test
    void testForGetAllSuccess_ShouldReturnListOfOrderResponseDto() {
        List<Order> orders = List.of(new Order(), new Order());
        List<OrderResponseDto> orderResponseDtoList = List.of(new OrderResponseDto(), new OrderResponseDto());

        securityContextHolderConfigCorrectly();
        when(userService.getByEmail("user@example.com")).thenReturn(user);
        when(orderRepository.findByUserId(1L)).thenReturn(orders);
        when(orderMapper.ordersRoOrderResponseDtoList(orders, user)).thenReturn(orderResponseDtoList);

        List<OrderResponseDto> all = orderService.getAll();
        assertEquals(2, all.size());
        verify(orderRepository).findByUserId(1L);
    }

    @Test
    void testForGetBYId_ShouldReturnOrderResponseDto() {
        Long orderId = 1L;
        Order order = new Order();
        OrderResponseDto orderResponseDto = new OrderResponseDto();

        securityContextHolderConfigCorrectly();
        when(userService.getByEmail("user@example.com")).thenReturn(user);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.orderToOrderResponseDto(order, user)).thenReturn(orderResponseDto);

        OrderResponseDto byId = orderService.getById(orderId);
        assertEquals(orderResponseDto, byId);
    }

    @Test
    void testForGetBYId_WhenUserNotFound_ShouldReturnResourceNotFoundException() {
        securityContextHolderConfigCorrectly();
        when(userService.getByEmail("user@example.com")).thenReturn(user);

        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.getById(1L);
        });

        assertEquals("Order can not found 1", resourceNotFoundException.getMessage());
    }

    @Test
    void testForGetBYId_WhenOrderNotFound_ShouldReturnResourceNotFoundException() {
        Long orderId = 1L;

        securityContextHolderConfigCorrectly();
        when(userService.getByEmail("user@example.com")).thenReturn(user);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.getById(orderId);
        });

        assertTrue(resourceNotFoundException.getMessage().contains("Order can not found"));
    }

    @Test
    void testForOrderDetails_ShouldReturnOrderDetailsDto() {
        Long orderId = 1L;

        Order order = new Order();
        order.setOrderItemList(List.of(new OrderItem(1L, order, 1L, 1L, 2, "BLUE", "M", BigDecimal.valueOf(200), BigDecimal.valueOf(50))));
        order.setOrderStatus(OrderStatus.SHIPPED);
        order.setTotalPrice(BigDecimal.valueOf(400));
        order.setAddress(new Address());

        securityContextHolderConfigCorrectly();
        when(orderRepository.findWithItemsByOrderId(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userService.getByEmail("user@example.com")).thenReturn(user);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderDetailsDto orderDetails = orderService.getOrderDetails(orderId);
        assertEquals(orderId, orderDetails.getOrderId());
        assertFalse(orderDetails.getFirmDetailsDtos().isEmpty());
    }

    @Test
    void testForOrderFilter_ShouldReturnFilteredOrder() {
        OrderFilterRequest orderFilterRequest = new OrderFilterRequest();
        orderFilterRequest.setDateType(DateType.MONTH);
        orderFilterRequest.setDateAmount(1);

        securityContextHolderConfigCorrectly();
        when(userService.getByEmail("user@example.com")).thenReturn(user);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery query = mock(CriteriaQuery.class);
        Root root = mock(Root.class);
        TypedQuery typedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Order.class)).thenReturn(query);
        when(query.from(Order.class)).thenReturn(root);
        when(entityManager.createQuery(query)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(new Order()));

        List<Order> orders = orderService.filterOrders(orderFilterRequest);
        assertEquals(1, orders.size());
    }

    private static void securityContextHolderConfigCorrectly() {
        String userEmail = "user@example.com";
        securityContextHolderConfig(userEmail);
    }

    private static void securityContextHolderConfigWrong() {
        String anonymousUser = "anonymousUser";
        securityContextHolderConfig(anonymousUser);
    }

    private static void securityContextHolderConfig(String userEmail) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userEmail);

        SecurityContextHolder.setContext(securityContext);
    }
}