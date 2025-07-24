package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.order.OrderDetailsDto;
import com.sarkhan.backend.dto.order.OrderFilterRequest;
import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.dto.order.OrderResponseDto;
import com.sarkhan.backend.exception.NotEnoughQuantityException;
import com.sarkhan.backend.model.order.Order;

import java.util.List;

public interface OrderService {
    List<OrderResponseDto> getAll();

    OrderResponseDto getById(Long orderId);

    String createOrder(OrderRequest orderRequest) throws NotEnoughQuantityException;

    List<Order> filterOrders(OrderFilterRequest orderFilterRequest);

    OrderDetailsDto getOrderDetails(Long orderId);
}
