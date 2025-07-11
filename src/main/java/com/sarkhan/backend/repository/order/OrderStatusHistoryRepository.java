package com.sarkhan.backend.repository.order;

import com.sarkhan.backend.model.order.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory,Long> {
    Optional<OrderStatusHistory> findLastByOrder_OrderIdOrderByOrderDateDesc(Long orderId);
}
