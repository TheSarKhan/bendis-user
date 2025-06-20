package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.OrderItem;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.repository.order.OrderItemRepository;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.service.SellerDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerDashboardServiceImpl implements SellerDashboardService {
    private final OrderItemRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public BigDecimal getTotalRevenue(Long sellerId) {
        return orderRepository.getTotalRevenue(sellerId);
    }

    @Override
    public Integer getTotalSales(Long sellerId) {
        return orderRepository.getTotalSales(sellerId);
    }

    @Override
    public Integer getCanceledSales(Long sellerId, OrderStatus orderStatus) {
        return null;
    }

    @Override
    public List<OrderItem> findTop10BySellerIdOrderByOrderDateDesc(Long sellerId) {
        return orderRepository.findTop10BySellerIdOrderByOrderDateDesc(sellerId);
    }

    @Override
    public List<Map<String, Object>> getMonthlyOrders(Long sellerId) {
        return orderRepository.getMonthlyOrders(sellerId);
    }

    @Override
    public List<Product> findTop5BySellerIdOrderBySalesCountDesc(Long sellerId) {
        return productRepository.findTop5BySellerIdOrderBySalesCountDesc(sellerId);
    }
}
