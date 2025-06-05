package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.exception.DataNotFoundException;
import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest, @RequestHeader("Authorization") String token) {
        token = token.substring(7);
        return ResponseEntity.status(201).body(orderService.createOrder(orderRequest, token));
    }

    //---------
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersBySellerAndFiltering(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate orderDate) throws DataNotFoundException {
        return ResponseEntity.ok(orderService.getOrdersBySellersAndFilters(token, status, orderDate));
    }
}
