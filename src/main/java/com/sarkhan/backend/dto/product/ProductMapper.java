package com.sarkhan.backend.dto.product;

import com.sarkhan.backend.model.comment.Comment;
import com.sarkhan.backend.model.product.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public static ProductResponse toResponse(Product product, Comment comment) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getOriginalPrice(),
                product.getDiscountedPrice(),
                product.getRating(),
                product.getSlug(),
                product.getColors(),
                product.getSpecifications(),
                comment != null ? comment.getText() : null,
                comment != null ? comment.getRating() : null
        );
    }

    public static ProductResponse toResponse(Product product) {
        return toResponse(product, null);
    }

}
