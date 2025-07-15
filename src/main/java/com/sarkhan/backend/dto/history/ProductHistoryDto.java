package com.sarkhan.backend.dto.history;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductHistoryDto {
    String productName;
    List<String> imageUrls;
}
