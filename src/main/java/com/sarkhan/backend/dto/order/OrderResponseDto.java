package com.sarkhan.backend.dto.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderResponseDto {
    Long orderId;
    String fullName;
    LocalDate orderDate;
    String orderStatus;
    String summary;
    BigDecimal totalPrice;
    String currency;
}
