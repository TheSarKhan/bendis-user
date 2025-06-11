package com.sarkhan.backend.repository.order;

import com.sarkhan.backend.model.order.OrderStatus;
import com.sarkhan.backend.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select sum(o.totalPrice) from Order o where o.sellerId = :sellerId")
    BigDecimal getTotalRevenue(Long sellerId);

    @Query("select count(o) from Order o where o.sellerId = :sellerId")
    Integer getTotalSales(Long sellerId);

    @Query("select count(o) from Order o where o.sellerId = :sellerId and o.orderStatus = :orderStatus")
    Integer getCanceledSales(Long sellerId, OrderStatus orderStatus);

    List<Order> findTop10BySellerIdOrderByOrderDateDesc(Long sellerId);

    @Query("select new map(extract(month from o.orderDate) as month, sum(o.totalPrice) as income) " +
            "from Order o where o.sellerId = :sellerId group by extract(month from o.orderDate)")
    List<Map<String, Object>> getMonthlyOrders(Long sellerId);

    @Query("SELECT o FROM Order o WHERE o.sellerId = :sellerId " +
            "AND (:status IS NULL OR o.orderStatus = :status) " +
            "AND (:startDate IS NULL OR o.orderDate >= :orderDate)")
    List<Order> findBySellerAndFilters(Long sellerId, OrderStatus status, LocalDate orderDate);

}
