package com.sarkhan.backend.dto.product;

import com.sarkhan.backend.model.product.items.Color;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductResponse(
        Long id,
        String name,
        String brand,
        BigDecimal originalPrice,
        BigDecimal discountedPrice,
        Double rating,
        String slug,
        List<Color> colors,
        Map<String, String> specifications,

        // Yeni sahələr (istifadəçiyə aid)
        String userCommentText,
        Integer userRating
) {}



