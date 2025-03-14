package com.sarkhan.backend.model.product.items;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Color {
    String color;
    int photoCount;
    Double stock;
    List<String> images;
}
