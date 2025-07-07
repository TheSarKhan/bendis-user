package com.sarkhan.backend.payment.dto.response;

import com.sarkhan.backend.model.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PayriffInvoiceResponse {
    private String code;
    private String message;
    private String route;
    private String internalMessage;
    private String responseId;
    private Payload payload;

    @Data
    public static class Payload {
        private String invoiceUuid;
        private String paymentUrl;
//        private BigDecimal amount;
//        private PaymentStatus invoiceStatus;

    }
}

