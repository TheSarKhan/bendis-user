package com.sarkhan.backend.service.impl.seller;

import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.OrderItem;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.repository.order.OrderItemRepository;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.service.impl.SellerDashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SellerDashboardServiceTest {
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private SellerDashboardServiceImpl sellerDashboardService;


    Long sellerId;

    @BeforeEach
    void setUp() {
        sellerId = 1L;
    }

    @Test
    void testGetTotalRevenue() {
        BigDecimal revenue = new BigDecimal("1234.55");

        when(orderItemRepository.getTotalRevenue(sellerId)).thenReturn(revenue);
        BigDecimal totalRevenue = sellerDashboardService.getTotalRevenue(sellerId);
        assertEquals(totalRevenue, revenue);
        verify(orderItemRepository).getTotalRevenue(sellerId);
    }

    @Test
    void testGetTotalSales() {
        when(orderItemRepository.getTotalSales(sellerId)).thenReturn(100);
        Integer totalSales = sellerDashboardService.getTotalSales(sellerId);
        assertEquals(100,totalSales);
    }

    @Test
    void testFind10Orders() {
        List<OrderItem> orderItems = List.of(new OrderItem(), new OrderItem());

        when(orderItemRepository.findTop10BySellerIdOrderByOrderDateDesc(sellerId)).thenReturn(orderItems);
        List<OrderItem> top10BySellerIdOrderByOrderDateDesc = sellerDashboardService.findTop10BySellerIdOrderByOrderDateDesc(sellerId);
        assertEquals(2,top10BySellerIdOrderByOrderDateDesc.size());
    }

    @Test
    void testGetMonthlyOrders() {
        List<Map<String, Object>> mapList = List.of(
                Map.of("month", 5, "income", new BigDecimal("200")),
                Map.of("month", 3, "income", new BigDecimal("500"))
        );
        when(orderItemRepository.getMonthlyOrders(sellerId)).thenReturn(mapList);
        List<Map<String, Object>> monthlyOrders = sellerDashboardService.getMonthlyOrders(sellerId);

        assertEquals(2,monthlyOrders.size());
        assertEquals(5,monthlyOrders.getFirst().get("month"));
    }

    @Test
    void testTop5Products() {
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findTop5BySellerIdOrderBySalesCountDesc(sellerId)).thenReturn(products);

        List<Product> result = sellerDashboardService.findTop5BySellerIdOrderBySalesCountDesc(sellerId);
        assertEquals(2,result.size());
    }
    @Test
    void testCanceledSales_ReturnNull() {
        Integer canceledSales = sellerDashboardService.getCanceledSales(sellerId, OrderStatus.CANCELLED);
        assertEquals(0,canceledSales);
    }
}
