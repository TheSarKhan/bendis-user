package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.history.PaymentHistoryRequestDto;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.model.order.PaymentHistory;
import com.sarkhan.backend.repository.order.OrderRepository;
import com.sarkhan.backend.repository.order.PaymentHistoryRepository;
import com.sarkhan.backend.service.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentHistoryServiceImpl implements PaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final OrderRepository orderRepository;

    @Override
    public void logPayment(PaymentHistoryRequestDto paymentRequestDto) {
        Order order = orderRepository.findById(paymentRequestDto.getOrderId()).orElseThrow();
        PaymentHistory paymentHistory = PaymentHistory.builder()
                .order(order)
                .paymentStatus(paymentRequestDto.getPaymentStatus())
                .cardLastFourDigits(paymentRequestDto.getCardLast4Digits())
                .paidDate(LocalDate.now())
                .build();
        paymentHistoryRepository.save(paymentHistory);
    }
}
