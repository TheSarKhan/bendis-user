package com.sarkhan.backend.dto.product;

import java.math.BigDecimal;

public record ProductResponseForGroupOfProduct(
        String imageUrl,
        boolean isFavorite,
        long id,
        String name,
        String description,
        double rating,
        int ratingCount,
        BigDecimal originalPrice,
        BigDecimal discountPrice) {
}
