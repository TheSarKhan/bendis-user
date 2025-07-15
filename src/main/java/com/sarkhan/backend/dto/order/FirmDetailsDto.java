package com.sarkhan.backend.dto.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FirmDetailsDto {
    String firmName;
    Integer productCount;
    BigDecimal totalPrice;
    List<ProductOrderDetailsDto> productOrderDetailsDtoList;
}
