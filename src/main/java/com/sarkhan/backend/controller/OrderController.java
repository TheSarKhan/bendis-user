package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.order.OrderDetailsDto;
import com.sarkhan.backend.dto.order.OrderFilterRequest;
import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.dto.order.OrderResponseDto;
import com.sarkhan.backend.handler.exception.NotEnoughQuantityException;
import com.sarkhan.backend.model.enums.DateType;
import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Order", description = "Order-related operations")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders", description = "Returns a list of all orders")
    public ResponseEntity<List<OrderResponseDto>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("{id}")
    @Operation(summary = "Get order by ID", description = "Returns the order with the specified ID")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order with the provided information")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) throws NotEnoughQuantityException {
        return ResponseEntity.status(201).body(orderService.createOrder(orderRequest));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter orders", description = "Returns a list of orders based on the provided filter criteria")
    public ResponseEntity<List<Order>> filterOrders(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false) DateType dateType,
            @RequestParam(required = false) Integer dateAmount,
            @RequestParam(required = false) Integer specificYear) {

        OrderFilterRequest filter = OrderFilterRequest.builder()
                .productName(productName)
                .orderStatus(orderStatus)
                .dateType(dateType)
                .dateAmount(dateAmount)
                .specificYear(specificYear)
                .build();

        return ResponseEntity.ok(orderService.filterOrders(filter));
    }


    @GetMapping("/getDetails/{orderId}")
    @Operation(summary = "Get order details", description = "Returns detailed information about a specific order")
    public ResponseEntity<OrderDetailsDto> getDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }
    @PostMapping("/change-status")
     public ResponseEntity<List<OrderResponseDto>> changeOrderStatus(@RequestParam Long orderId, @RequestParam OrderStatus status){
        return ResponseEntity.ok(orderService.changeOrderStatus(orderId, status));
    }

    @GetMapping("/test")
    public ResponseEntity<List<Order>> get() {
        return ResponseEntity.ok(orderService.getForTest());
    }

}
