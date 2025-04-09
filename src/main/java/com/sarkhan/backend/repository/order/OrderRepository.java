package com.sarkhan.backend.repository.order;

import com.sarkhan.backend.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
