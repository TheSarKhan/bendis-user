package com.sarkhan.backend.controller;

import com.sarkhan.backend.model.order.OrderStatus;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.service.SellerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/seller/dashboard")
@RequiredArgsConstructor
public class SellerDashboardController {
    private final SellerDashboardService sellerDashboardService;

    @GetMapping("/total-revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue(@RequestParam Long sellerId) {
        return ResponseEntity.ok(sellerDashboardService.getTotalRevenue(sellerId));
    }

    @GetMapping("/total-sales")
    public ResponseEntity<Integer> getTotalSales(@RequestParam Long sellerId) {
        return ResponseEntity.ok(sellerDashboardService.getTotalSales(sellerId));
    }

    @GetMapping("/canceled-sales")
    public ResponseEntity<Integer> getCanceledSales(@RequestParam Long sellerId, @RequestParam OrderStatus orderStatus) {
        return ResponseEntity.ok(sellerDashboardService.getCanceledSales(sellerId, orderStatus));
    }

    @GetMapping("/recent-orders")
    public ResponseEntity<List<Order>> recentOrders(@RequestParam Long sellerId) {
        return ResponseEntity.ok(sellerDashboardService.findTop10BySellerIdOrderByOrderDateDesc(sellerId));
    }

    @GetMapping("/monthly-stats")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyOrders(@RequestParam Long sellerId) {
        return ResponseEntity.ok(sellerDashboardService.getMonthlyOrders(sellerId));
    }

    @GetMapping("/top-selling")
    public ResponseEntity<List<Product>> getTopSellingProducts(@RequestParam Long sellerId) {
        return ResponseEntity.ok(sellerDashboardService.findTop5BySellerIdOrderBySalesCountDesc(sellerId));
    }
}
