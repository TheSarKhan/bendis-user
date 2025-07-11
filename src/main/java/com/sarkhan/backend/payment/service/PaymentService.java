package com.sarkhan.backend.payment.service;

import com.sarkhan.backend.dto.order.OrderRequest;

public interface PaymentService {
    String createInvoice(OrderRequest orderRequest,String token);

    String getInvoice(String uuid);

}
