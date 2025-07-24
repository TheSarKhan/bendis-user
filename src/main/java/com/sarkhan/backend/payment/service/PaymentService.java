package com.sarkhan.backend.payment.service;

import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.model.user.User;

public interface PaymentService {
    String createInvoice(OrderRequest orderRequest,String token);

    String createInvoice(OrderRequest orderRequest, User user);

    String getInvoice(String uuid);

}
