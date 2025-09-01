package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.history.OrderHistoryResponseDto;
import com.sarkhan.backend.dto.history.ProductHistoryDto;
import com.sarkhan.backend.handler.exception.DataNotFoundException;
import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.model.order.OrderStatusHistory;
import com.sarkhan.backend.model.order.PaymentHistory;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.order.OrderRepository;
import com.sarkhan.backend.repository.order.OrderStatusHistoryRepository;
import com.sarkhan.backend.repository.order.PaymentHistoryRepository;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.OrderStatusHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusHistoryServiceImpl implements OrderStatusHistoryService {
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserRepository userRepository;

    @Override
    public void changeStatus(Long orderId, OrderStatus newOrderStatus) throws DataNotFoundException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("Order can not found {}", orderId);
            return new DataNotFoundException("Order can not found {}" + orderId);
        });
        if (order.getOrderStatus().name().equals(newOrderStatus.name())) return;
        order.setOrderStatus(newOrderStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        OrderStatusHistory orderStatusHistory = OrderStatusHistory.builder()
                .order(order)
                .changedAt(LocalDate.now())
                .orderStatus(newOrderStatus)
                .build();
        orderStatusHistoryRepository.save(orderStatusHistory);
    }

    @Override
    public List<OrderHistoryResponseDto> getOrderHistory(String userEmail) throws DataNotFoundException {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> {
            log.error("User can not found {}", userEmail);
            return new DataNotFoundException("User can not found {}" + userEmail);
        });
        List<Order> orders = orderRepository.findByUserId(user.getId());
        return orders.stream().map(order -> {
            List<ProductHistoryDto> productHistoryDtoList = order.getOrderItemList().stream().map((orderItem -> {
                Product product = productRepository.findById(orderItem.getProductId()).orElseThrow(() -> {
                    log.error("Product can not found {}", orderItem.getProductId());
                    return new NoSuchElementException("Product can not found {}" + orderItem.getProductId());
                });
                List<String> imageUrls = product.getColorAndSizes().stream().flatMap(item -> {
                    List<String> images = item.getImageUrls();
                    return (images != null && !images.isEmpty()) ? images.stream() : Stream.empty();
                }).toList();
                ProductHistoryDto productHistoryDto = new ProductHistoryDto();
                productHistoryDto.setProductName(product.getName());
                productHistoryDto.setImageUrls(imageUrls);
                return productHistoryDto;
            })).toList();

            OrderStatusHistory lastStatus = orderStatusHistoryRepository.findLastByOrder_OrderIdOrderByChangedAtDesc(order.getOrderId()).orElse(null);
            PaymentHistory lastPayment = paymentHistoryRepository.findLastPaymentByOrder_OrderIdOrderByPaidDateDesc(order.getOrderId()).orElse(null);
            String card = lastPayment != null ? lastPayment.getCardLastFourDigits() : "****";
            OrderStatus status = lastStatus != null ? lastStatus.getOrderStatus() : order.getOrderStatus();
            LocalDate lastStatusChangesAt = lastStatus != null ? lastStatus.getChangedAt() : order.getOrderDate();
            return new OrderHistoryResponseDto(
                    order.getOrderId(),
                    card,
                    order.getOrderDate(),
                    status,
                    order.getTotalPrice(),
                    productHistoryDtoList,
                    lastStatusChangesAt
            );
        }).toList();
    }
}
