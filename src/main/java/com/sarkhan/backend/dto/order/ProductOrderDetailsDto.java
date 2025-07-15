package com.sarkhan.backend.dto.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrderDetailsDto {
    String productName;
    String imageUrl;
    String color;
    String size;
    Integer quantity;
    String status;
}
