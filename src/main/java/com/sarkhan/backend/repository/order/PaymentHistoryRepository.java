package com.sarkhan.backend.repository.order;

import com.sarkhan.backend.model.order.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory,Long> {
    Optional<PaymentHistory> findLastPaymentByOrder_OrderIdOrderByPaidDateDesc(Long orderId);
}
