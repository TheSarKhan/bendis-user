package com.sarkhan.backend.model.product.items;

import com.sarkhan.backend.model.enums.Color;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ColorAndSize {
    Color color;

    Integer photoCount;

    Long stock;

    List<String> imageUrls;

    Map<String, Long> sizeStockMap;
}
