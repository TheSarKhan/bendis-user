package com.sarkhan.backend.repository.order;

import com.sarkhan.backend.model.order.OrderItem;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT DISTINCT oi.productId FROM OrderItem oi WHERE oi.userId = :userId")
    List<Long> findOrderedProductIdsByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}

