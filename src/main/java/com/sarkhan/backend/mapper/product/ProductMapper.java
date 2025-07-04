package com.sarkhan.backend.mapper.product;

import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.user.User;

import java.time.LocalDateTime;

public class ProductMapper {

    public static Product toEntity(ProductRequest request, User user) {
        return Product.builder()
                .name(request.name())
                .originalPrice(request.originalPrice())
                .discountedPrice(request.discountedPrice())
                .subCategoryId(request.subCategoryId())
                .sellerId(user.getId())
                .brand(user.getSeller() != null ? user.getSeller().getBrandName() : null) // burda null yoxlanılır
                .gender(request.gender())
                .description(request.description())
                .pluses(request.pluses())
                .colors(request.colors())
                .specifications(request.specifications())
                .build();
    }


    public static Product updateEntity(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setOriginalPrice(request.originalPrice());
        product.setDiscountedPrice(request.discountedPrice());
        product.setSubCategoryId(request.subCategoryId());
        product.setColors(request.colors());
        product.setGender(request.gender());
        product.setDescription(request.description());
        product.setPluses(request.pluses());
        product.setSpecifications(request.specifications());
        product.setUpdateAt(LocalDateTime.now());

        product.generateSlug();
        return product;
    }
}

