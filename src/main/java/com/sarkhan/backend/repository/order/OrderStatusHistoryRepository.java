package com.sarkhan.backend.repository.order;

import com.sarkhan.backend.model.order.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory,Long> {
    Optional<OrderStatusHistory> findLastByOrder_OrderIdOrderByChangedAtDesc(Long orderId);
}
