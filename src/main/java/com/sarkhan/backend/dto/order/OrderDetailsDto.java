package com.sarkhan.backend.dto.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailsDto {
    Long orderId;
    LocalDate orderDate;
    String orderStatus;
    List<FirmDetailsDto> firmDetailsDtos;
    OrderSummaryDto orderSummaryDto;
}
