package com.sarkhan.backend.controller;

import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.OrderItem;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.service.SellerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/seller/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerDashboardController {
    private final SellerDashboardService sellerDashboardService;

    @GetMapping("/total-revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue(@RequestHeader("Authorization") String token) {
        Long sellerId = sellerDashboardService.extractSellerIdFromToken(token);
        return ResponseEntity.ok(sellerDashboardService.getTotalRevenue(sellerId));
    }

    @GetMapping("/total-sales")
    public ResponseEntity<Integer> getTotalSales(@RequestHeader("Authorization") String token) {
        Long sellerId = sellerDashboardService.extractSellerIdFromToken(token);
        return ResponseEntity.ok(sellerDashboardService.getTotalSales(sellerId));
    }

    @GetMapping("/canceled-sales")
    public ResponseEntity<Integer> getCanceledSales(@RequestHeader("Authorization") String token, @RequestParam OrderStatus orderStatus) {
        Long sellerId = sellerDashboardService.extractSellerIdFromToken(token);
        return ResponseEntity.ok(sellerDashboardService.getCanceledSales(sellerId, orderStatus));
    }

    @GetMapping("/recent-orders")
    public ResponseEntity<List<OrderItem>> recentOrders(@RequestHeader("Authorization") String token) {
        Long sellerId = sellerDashboardService.extractSellerIdFromToken(token);
        return ResponseEntity.ok(sellerDashboardService.findTop10BySellerIdOrderByOrderDateDesc(sellerId));
    }

    @GetMapping("/monthly-stats")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyOrders(@RequestHeader("Authorization") String token) {
        Long sellerId = sellerDashboardService.extractSellerIdFromToken(token);
        return ResponseEntity.ok(sellerDashboardService.getMonthlyOrders(sellerId));
    }

    @GetMapping("/top-selling")
    public ResponseEntity<List<Product>> getTopSellingProducts(@RequestHeader("Authorization") String token) {
        Long sellerId = sellerDashboardService.extractSellerIdFromToken(token);
        return ResponseEntity.ok(sellerDashboardService.findTop5BySellerIdOrderBySalesCountDesc(sellerId));
    }
}
