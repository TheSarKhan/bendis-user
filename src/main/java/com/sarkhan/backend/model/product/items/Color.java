package com.sarkhan.backend.model.product.items;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Color {
    String color;

    int photoCount;

    Long stock;

    List<String> images;

    Map<String, Long> sizeStockMap;
}
