package com.sarkhan.backend.model.product.items;

import com.sarkhan.backend.model.enums.Color;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ColorAndSize {
    Color color;

    Integer photoCount;

    Long stock;

    List<String> imageUrls;

    Map<String, Long> sizeStockMap;
}
