package com.sarkhan.backend.mapper.order;

import com.sarkhan.backend.dto.order.OrderResponseDto;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final ProductRepository productRepository;

    public OrderResponseDto orderToOrderResponseDto(Order order, User user) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus().name())
                .orderDate(order.getOrderDate())
                .fullName(user.getFullName())
                .currency("₼")
                .summary(order.getOrderItemList().stream().map(orderItem -> getProductNameById(orderItem.getProductId())).toString())
                .build();
    }

    public List<OrderResponseDto> ordersRoOrderResponseDtoList(List<Order> orders, User user) {
        return orders.stream().map(order -> orderToOrderResponseDto(order, user)).toList();
    }

    public String getProductNameById(Long productId) {
        return productRepository.findById(productId).map(Product::getName).orElse("Unknown product name");
    }
}
