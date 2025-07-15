package com.sarkhan.backend.dto.history;

import com.sarkhan.backend.model.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderHistoryResponseDto {
    Long orderId;
    String cardLast4Number;
    LocalDate orderDate;
    OrderStatus orderStatus;
    BigDecimal totalPrice;
    List<ProductHistoryDto> productHistoryDtoList;
    LocalDate lastStatusChangedDate;

}
