package com.sarkhan.backend.dto.history;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductHistoryDto {
    String productName;
    String imageUrl;
}
