package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.history.OrderHistoryResponseDto;
import com.sarkhan.backend.handler.exception.DataNotFoundException;
import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.service.OrderStatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orderHistory")
@RequiredArgsConstructor
public class OrderHistoryController {
    private final OrderStatusHistoryService orderStatusHistoryService;

    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryResponseDto>> getOrderHistory(Authentication authentication) throws DataNotFoundException {
        String name = authentication.getName();
        return ResponseEntity.ok(orderStatusHistoryService.getOrderHistory(name));
    }

    @PostMapping("/orders/shipped/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> markAsShipped(@PathVariable Long orderId) throws DataNotFoundException {
        orderStatusHistoryService.changeStatus(orderId, OrderStatus.SHIPPED);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/cancel/{orderId}")
    public ResponseEntity<Void> canceledOrder(@PathVariable Long orderId) throws DataNotFoundException {
        orderStatusHistoryService.changeStatus(orderId, OrderStatus.CANCELLED);
        return ResponseEntity.ok().build();
    }
}
