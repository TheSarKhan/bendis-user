package com.sarkhan.backend.dto.comment;

import java.math.BigDecimal;

public record CommentResponseForMyComment(
        Long productId,
        String productImage,
        String productName,
        String productDescription,
        BigDecimal originalPrice,
        BigDecimal discountPrice,
        boolean isFavorite,
        double rating,
        long ratingCount,
        long commentId,
        String commentContent) {
}
