package com.sarkhan.backend.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.payment.dto.response.PaymentProviderResponse;

public interface PaymentService {
    String createInvoice(OrderRequest orderRequest,String token);

    String getInvoice(String uuid);
    PaymentProviderResponse getPaymentResult(String uuid) throws JsonProcessingException;

}
