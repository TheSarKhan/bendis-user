package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.history.PaymentHistoryRequestDto;

public interface PaymentHistoryService {
    void logPayment(PaymentHistoryRequestDto paymentHistoryRequestDto);

    String handleApprove(Long orderId, String cardLast4Digits);
}
