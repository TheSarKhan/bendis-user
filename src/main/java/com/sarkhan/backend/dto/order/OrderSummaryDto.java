package com.sarkhan.backend.dto.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderSummaryDto {
    BigDecimal toralPrice;
    BigDecimal discount;
    BigDecimal deliveryFee;
    BigDecimal finalPrice;
    String fullName;
    String phoneNumber;
    String address;
}
