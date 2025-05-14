package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.order.OrderRequest;

public interface OrderService {
    String createOrder(OrderRequest orderRequest,String token);
}
