package com.sarkhan.backend.repository.order;

import com.sarkhan.backend.model.order.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory,Long> {
    Optional<PaymentHistory> findLastPaymentByOrderIdOrderByPaidDateDesc(Long orderId);
}
