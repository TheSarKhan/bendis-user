package com.sarkhan.backend.dto.order;

import com.sarkhan.backend.model.enums.DateType;
import com.sarkhan.backend.model.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderFilterRequest {
    String productName;
    OrderStatus orderStatus;
    DateType dateType;
    Integer dateAmount;
    Integer specificYear;
}
