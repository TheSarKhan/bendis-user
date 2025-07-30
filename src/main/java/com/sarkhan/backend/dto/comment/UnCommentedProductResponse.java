package com.sarkhan.backend.dto.comment;

import java.math.BigDecimal;

public record UnCommentedProductResponse(Long productId,
                                         String productImage,
                                         String productName,
                                         String productDescription,
                                         BigDecimal originalPrice,
                                         BigDecimal discountPrice,
                                         boolean isFavorite,
                                         double rating,
                                         long ratingCount) {
}
