package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.order.OrderFilterRequest;
import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.exception.NotEnoughQuantityException;
import com.sarkhan.backend.model.order.Order;

import java.util.List;

public interface OrderService {
    String createOrder(OrderRequest orderRequest, String token) throws NotEnoughQuantityException;
    List<Order> filterOrders(OrderFilterRequest orderFilterRequest,String token);
}
