package com.sarkhan.backend.repository.order;

import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.model.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select sum(o.totalPrice) from OrderItem o where o.sellerId = :sellerId")
    BigDecimal getTotalRevenue(Long sellerId);

    @Query("select count(o) from OrderItem o where o.sellerId = :sellerId")
    Integer getTotalSales(Long sellerId);

    @Query("select count(o) from OrderItem o where o.sellerId = :sellerId and o.order.orderStatus = :orderStatus")
    Integer getCanceledSales(Long sellerId, OrderStatus orderStatus);

    @Query("select o from OrderItem o where o.sellerId=:sellerId order by o.order.orderDate desc ")
    List<OrderItem> findTop10BySellerIdOrderByOrderDateDesc(Long sellerId);

    @Query("select new map(function('MONTH',o.order.orderDate) as month, sum(o.totalPrice) as income) " +
            "from OrderItem o where o.sellerId = :sellerId group by function('MONTH',o.order.orderDate)")
    List<Map<String, Object>> getMonthlyOrders(Long sellerId);

    @Query("SELECT o FROM OrderItem o WHERE o.sellerId = :sellerId " +
            "AND (:status IS NULL OR o.order.orderStatus = :status) " +
            "AND (:startDate IS NULL OR o.order.orderDate >= :orderDate)")
    List<Order> findBySellerAndFilters(Long sellerId, OrderStatus status, LocalDate orderDate);
}
