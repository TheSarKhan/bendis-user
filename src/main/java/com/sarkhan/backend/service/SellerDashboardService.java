package com.sarkhan.backend.service;

import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.OrderItem;
import com.sarkhan.backend.model.product.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SellerDashboardService {
    BigDecimal getTotalRevenue(Long sellerId);

    Integer getTotalSales(Long sellerId);

    Integer getCanceledSales(Long sellerId, OrderStatus orderStatus);

    List<OrderItem> findTop10BySellerIdOrderByOrderDateDesc(Long sellerId);

    List<Map<String, Object>> getMonthlyOrders(Long sellerId);

    List<Product> findTop5BySellerIdOrderBySalesCountDesc(Long sellerId);
}
