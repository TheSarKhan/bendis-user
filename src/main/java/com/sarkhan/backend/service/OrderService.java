package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.exception.NotEnoughQuantityException;

public interface OrderService {
    String createOrder(OrderRequest orderRequest,String token) throws NotEnoughQuantityException;
}
