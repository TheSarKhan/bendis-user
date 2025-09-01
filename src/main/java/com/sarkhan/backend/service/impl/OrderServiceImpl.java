package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.order.*;
import com.sarkhan.backend.exception.NotEnoughQuantityException;
import com.sarkhan.backend.handler.exception.ResourceNotFoundException;
import com.sarkhan.backend.mapper.order.OrderMapper;
import com.sarkhan.backend.model.cart.Cart;
import com.sarkhan.backend.model.cart.CartItem;
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
import com.sarkhan.backend.service.OrderService;
import com.sarkhan.backend.service.UserService;
import com.sarkhan.backend.service.impl.product.util.UserUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final UserService userService;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final EntityManager entityManager;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderResponseDto> getAll() {
        User user = getCurrentUser();
        return orderMapper.ordersRoOrderResponseDtoList(
                orderRepository.findByUserId(user.getId()), user);
    }

    @Override
    public OrderResponseDto getById(Long orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("Order can not found for this user:{}", orderId);
            return new ResourceNotFoundException("Order can not found " + orderId);
        });
        return orderMapper.orderToOrderResponseDto(order, user);
    }

    @Override
    @Transactional
    public String createOrder(OrderRequest orderRequest) throws NotEnoughQuantityException {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> {
            log.error("Cart can not found for this user:{}", user.getId());
            return new ResourceNotFoundException("Cart can not found for this user");
        });
        Address address = Address.builder()
                .finCode(orderRequest.getAddressRequestDto().getFinCode())
                .city(orderRequest.getAddressRequestDto().getCity())
                .region(orderRequest.getAddressRequestDto().getRegion())
                .street(orderRequest.getAddressRequestDto().getStreet())
                .postalCode(orderRequest.getAddressRequestDto().getPostalCode())
                .userId(user.getId())
                .build();
        addressRepository.save(address);

        List<CartItemRequestDTO> items = orderRequest.getItems();

        for (CartItemRequestDTO item : items) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> {
                log.error("Product can not found");
                return new ResourceNotFoundException("Product can not found: ID = " + item.getProductId());
            });

            List<ColorAndSize> colorAndSizes = product.getColorAndSizes();
            ColorAndSize colorAndSize = colorAndSizes.stream()
                    .filter(c -> c.getColor().name().equalsIgnoreCase(item.getColor()))
                    .findFirst().orElseThrow(() -> {
                        log.error("Color can not found");
                        return new ResourceNotFoundException("Color can not found:" + item.getColor());
                    });
            if (item.getSize() != null && !item.getSize().isBlank()) {
                Long sizeStock = colorAndSize.getSizeStockMap().get(item.getSize());
                if (sizeStock == null) {
                    log.error("Size not found: {}", item.getSize());
                    throw new ResourceNotFoundException("Size not found: " + item.getSize());
                }
                if (item.getQuantity() > sizeStock) {
                    log.error("Not enough quantity:" + item.getQuantity());
                    throw new NotEnoughQuantityException("Not enough quantity");
                }
                colorAndSize.getSizeStockMap().put(item.getSize(), sizeStock - item.getQuantity());
            } else {
                if (item.getQuantity() > colorAndSize.getStock()) {
                    log.error("Not enough quantity: " + item.getQuantity());
                    throw new NotEnoughQuantityException("Not enough quantity");
                }
                colorAndSize.setStock(colorAndSize.getStock() - item.getQuantity());
            }
            product.setSalesCount(product.getSalesCount() + item.getQuantity());
            productRepository.save(product);
        }
        BigDecimal sum = cart.getCartItems().stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        Order create = Order.builder()
                .address(address)
                .userId(user.getId())
                .totalPrice(sum)
                .cartId(cart.getId())
                .orderStatus(OrderStatus.PENDING)
                .build();

        Order order = orderRepository.save(create);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(cartItem.getProductId())
                    .quantity(cartItem.getQuantity())
                    .totalPrice(cartItem.getTotalPrice())
                    .color(cartItem.getColor())
                    .build();
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);

        cartItemRepository.deleteAll(cart.getCartItems());

        return paymentService.createInvoice(orderRequest, user);
    }

    @Override
    public List<Order> filterOrders(OrderFilterRequest orderFilterRequest) {
        User user = getCurrentUser();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = criteriaBuilder.createQuery(Order.class);
        Root<Order> order = query.from(Order.class);
        Join<Order, OrderItem> itemList = order.join("orderItemList", JoinType.LEFT);
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.now();
        if ("DAY".equalsIgnoreCase(orderFilterRequest.getDateType().name()) && orderFilterRequest.getDateAmount() != null) {
            startDate = LocalDate.now().minusDays(orderFilterRequest.getDateAmount());
        }
        if ("MONTH".equalsIgnoreCase(orderFilterRequest.getDateType().name()) && orderFilterRequest.getDateAmount() != null) {
            startDate = LocalDate.now().minusMonths(orderFilterRequest.getDateAmount());
        }
        if ("YEAR".equalsIgnoreCase(orderFilterRequest.getDateType().name())) {
            if (orderFilterRequest.getSpecificYear() != null) {
                startDate = LocalDate.of(orderFilterRequest.getSpecificYear(), 1, 1);
                endDate = LocalDate.of(orderFilterRequest.getSpecificYear(), 12, 31);
            } else if (orderFilterRequest.getDateAmount() != null) {
                startDate = LocalDate.now().minusYears(orderFilterRequest.getDateAmount());

            }
        }
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(order.get("userId"), user.getId()));
        if (startDate != null) {
            predicates.add(criteriaBuilder.between(order.get("orderDate"), startDate, endDate));
        }
        if (orderFilterRequest.getOrderStatus() != null) {
            predicates.add(criteriaBuilder.equal(order.get("orderStatus"), orderFilterRequest.getOrderStatus()));
        }
        if (orderFilterRequest.getProductName() != null && !orderFilterRequest.getProductName().isEmpty()) {
            List<Long> idsFromName = productRepository.findIdsFromName(orderFilterRequest.getProductName());
            if (!idsFromName.isEmpty()) {
                predicates.add(itemList.get("id").in(idsFromName));
            } else {
                predicates.add(criteriaBuilder.disjunction());
            }
        }
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    @Transactional
    public OrderDetailsDto getOrderDetails(Long orderId) {
        Order order = orderRepository.findWithItemsByOrderId(orderId).orElseThrow(() -> {
            log.error("Order can not found:" + orderId);
            return new ResourceNotFoundException("Order can not found {}" + orderId);
        });
        Map<String, FirmDetailsDto> firmDetailsDtoMap = new HashMap<>();

        for (OrderItem orderItem : order.getOrderItemList()) {
            Product product = productRepository.findById(orderItem.getProductId()).orElseThrow(() -> {
                log.error("Product can not found:" + orderItem.getProductId());
                return new ResourceNotFoundException("Product can not found {}" + orderItem.getProductId());
            });

            String brand = product.getBrand();
            String imageUrl = product.getColorAndSizes().stream().filter(colorAndSize -> colorAndSize.getColor().name().equals(orderItem.getColor()))
                    .findFirst().map(colorAndSize -> {
                        List<String> imageUrls = colorAndSize.getImageUrls();
                        return (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.getFirst() : null;
                    }).orElse(null);
            ProductOrderDetailsDto productOrderDetailsDto = ProductOrderDetailsDto.builder()
                    .productName(product.getName())
                    .imageUrl(imageUrl)
                    .color(orderItem.getColor())
                    .status(order.getOrderStatus().name())
                    .quantity(orderItem.getQuantity())
                    .size(orderItem.getSize())
                    .build();
            firmDetailsDtoMap.putIfAbsent(brand, new FirmDetailsDto(brand, 0, BigDecimal.ZERO, new ArrayList<>()));

            FirmDetailsDto firmDetailsDto = firmDetailsDtoMap.get(brand);
            firmDetailsDto.getProductOrderDetailsDtoList().add(productOrderDetailsDto);
            firmDetailsDto.setProductCount(firmDetailsDto.getProductCount() + 1);
            BigDecimal currentTotal = firmDetailsDto.getTotalPrice() != null
                    ? firmDetailsDto.getTotalPrice()
                    : BigDecimal.ZERO;

            BigDecimal itemPrice = orderItem.getTotalPrice() != null
                    ? orderItem.getTotalPrice()
                    : BigDecimal.ZERO;

            firmDetailsDto.setTotalPrice(currentTotal.add(itemPrice));
        }
        OrderSummaryDto orderSummaryDto = orderSummaryDto(orderId);
        return OrderDetailsDto.builder()
                .orderId(orderId)
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus().name())
                .firmDetailsDtos(new ArrayList<>(firmDetailsDtoMap.values()))
                .orderSummaryDto(orderSummaryDto)
                .build();
    }

    @Override
    public List<OrderResponseDto> changeOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("Order can not found:" + orderId);
            return new ResourceNotFoundException("Order can not found {}" + orderId);
        });
        order.setOrderStatus(status);
        orderRepository.save(order);
        User user = getCurrentUser();
        List<Order> orders = orderRepository.findByOrderStatus(status);
        return orderMapper.ordersRoOrderResponseDtoList(orders,user);
    }

    private OrderSummaryDto orderSummaryDto(Long orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("Order can not found:" + orderId);
            return new ResourceNotFoundException("Order can not found {}" + orderId);
        });
        Address address = order.getAddress();
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal deliveryFee = BigDecimal.ZERO;
        for (OrderItem orderItem : order.getOrderItemList()) {
            Product product = productRepository.findById(orderItem.getProductId()).orElseThrow(() -> {
                log.error("Product can not found:" + orderItem.getProductId());
                return new ResourceNotFoundException("Product can not found {}" + orderItem.getProductId());
            });
            discount = discount.add((product.getOriginalPrice().subtract(product.getDiscountedPrice()))
                    .multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            deliveryFee = deliveryFee.add(orderItem.getDeliveryFee());
        }
        BigDecimal finalPrice = order.getTotalPrice().subtract(discount).add(deliveryFee);

        return OrderSummaryDto.builder()
                .toralPrice(order.getTotalPrice())
                .discount(discount)
                .deliveryFee(deliveryFee)
                .finalPrice(finalPrice)
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(address.getRegion() + "," + address.getCity() + "," +
                        address.getStreet() + "," + address.getPostalCode())
                .build();
    }

    private User getCurrentUser() {
        try {
            return UserUtil.getCurrentUser(userService, log);
        } catch (AuthException e) {
            log.error("User not found");
            return null;
        }
    }

    @Override
    public List<Order> getForTest() {
        return orderRepository.findAll();
    }
}
