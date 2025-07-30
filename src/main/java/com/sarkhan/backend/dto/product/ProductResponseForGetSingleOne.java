package com.sarkhan.backend.dto.product;

import com.sarkhan.backend.dto.comment.CommentResponse;
import com.sarkhan.backend.model.enums.Gender;
import com.sarkhan.backend.model.product.items.ColorAndSize;
import com.sarkhan.backend.model.product.items.Plus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ProductResponseForGetSingleOne(
        Long productId,
        String productName,
        BigDecimal originalPrice,
        BigDecimal discountPrice,
        String subCategoryName,
        Long sellerId,
        String sellerName,
        String brandName,
        Gender gender,
        String description,
        String slug,
        Integer salesCount,
        Long totalStock,
        Double rating,
        Map<Long, Double> ratings,
        List<Plus> pluses,
        List<ColorAndSize> colorAndSizes,
        Map<String, String> specifications,
        LocalDateTime dateTime,
        List<CommentResponse> comments) {
}
