package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest, @RequestHeader("Authorization") String token) {
        token=token.substring(7);
 return ResponseEntity.status(201).body( orderService.createOrder(orderRequest,token));
    }
}
