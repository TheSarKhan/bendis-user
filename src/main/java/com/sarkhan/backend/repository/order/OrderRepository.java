package com.sarkhan.backend.repository.order;

import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "orderItemList")
    List<Order> findByUserId(Long userId);

    @EntityGraph(attributePaths = "orderItemList")
    Optional<Order> findWithItemsByOrderId(Long orderId);

    List<Order> findByOrderStatus(OrderStatus status);

}
