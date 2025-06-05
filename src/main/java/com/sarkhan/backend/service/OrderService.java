package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.exception.DataNotFoundException;
import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    String createOrder(OrderRequest orderRequest, String token);

    List<Order> getOrdersBySellersAndFilters(String token, OrderStatus status, LocalDate orderDate) throws DataNotFoundException;
}
