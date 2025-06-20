package com.sarkhan.backend.mapper.product;

import com.sarkhan.backend.dto.product.items.ColorAndSizeRequest;
import com.sarkhan.backend.model.product.items.ColorAndSize;

public class ColorAndSizeMapper {
    public static ColorAndSize toEntity(ColorAndSizeRequest request) {
        return ColorAndSize.builder().
                color(request.color()).
                stock(request.stock()).
                photoCount(request.photoCount()).
                sizeStockMap(request.sizeStockMap()).
                build();
    }
}
