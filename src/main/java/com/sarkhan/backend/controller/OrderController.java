package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.order.OrderFilterRequest;
import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.dto.order.OrderResponseDto;
import com.sarkhan.backend.exception.NotEnoughQuantityException;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAll(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        return ResponseEntity.ok(orderService.getAll(token));
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        token = token.substring(7);
        return ResponseEntity.ok(orderService.getById(id, token));
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest, @RequestHeader("Authorization") String token) throws NotEnoughQuantityException {
        token = token.substring(7);
        return ResponseEntity.status(201).body(orderService.createOrder(orderRequest, token));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Order>> filterOrders(@RequestBody OrderFilterRequest orderFilterRequest, @RequestHeader("Authorization") String token) {
        token = token.substring(7);
        return ResponseEntity.ok(orderService.filterOrders(orderFilterRequest, token));
    }
}
