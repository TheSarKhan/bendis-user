package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.history.OrderHistoryResponseDto;
import com.sarkhan.backend.exception.DataNotFoundException;
import com.sarkhan.backend.model.enums.OrderStatus;

import java.util.List;

public interface OrderStatusHistoryService {
    void changeStatus(Long orderId, OrderStatus orderStatus) throws DataNotFoundException;
    List<OrderHistoryResponseDto> getOrderHistory(String userEmail) throws DataNotFoundException;
}
