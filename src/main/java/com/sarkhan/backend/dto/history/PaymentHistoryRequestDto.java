package com.sarkhan.backend.dto.history;

import com.sarkhan.backend.model.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentHistoryRequestDto {
    Long orderId;
    String cardLast4Digits;
    PaymentStatus paymentStatus;
}
