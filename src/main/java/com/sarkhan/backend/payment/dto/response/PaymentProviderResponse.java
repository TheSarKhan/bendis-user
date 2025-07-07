package com.sarkhan.backend.payment.dto.response;

import com.sarkhan.backend.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProviderResponse {
    private String transactionId;
    private BigDecimal amount;
    private String cardLastFourDigits;
    private String paymentStatus;
}
