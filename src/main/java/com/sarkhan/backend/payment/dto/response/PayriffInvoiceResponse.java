package com.sarkhan.backend.payment.dto.response;

import lombok.Data;
@Data
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
        private String paymentUrl; // <<< En önemli yer burası
    }
}

