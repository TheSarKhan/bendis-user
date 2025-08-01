package com.sarkhan.backend.mapper.product;

import com.sarkhan.backend.dto.comment.CommentResponse;
import com.sarkhan.backend.dto.product.ProductRequest;
import com.sarkhan.backend.dto.product.ProductResponseForGetSingleOne;
import com.sarkhan.backend.dto.product.ProductResponseForGroupOfProduct;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.Plus;
import com.sarkhan.backend.model.product.items.SubCategory;
import com.sarkhan.backend.model.user.Seller;
import com.sarkhan.backend.model.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ProductMapper {

    public static Product toEntity(ProductRequest request, User user) {
        return Product.builder()
                .name(request.name())
                .originalPrice(request.originalPrice())
                .discountedPrice(request.discountedPrice())
                .subCategoryId(request.subCategoryId())
                .sellerId(user.getId())
                .brand(user.getSeller().getBrandName())
                .gender(request.gender())
                .description(request.description())
                .pluses(request.pluses())
                .colorAndSizes(request.colorAndSizeRequests().
                        stream().
                        map(ColorAndSizeMapper::toEntity).
                        toList())
                .specifications(request.specifications())
                .build();
    }

    public static Product updateEntity(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setOriginalPrice(request.originalPrice());
        product.setDiscountedPrice(request.discountedPrice());
        product.setSubCategoryId(request.subCategoryId());
        product.setColorAndSizes(request.colorAndSizeRequests().
                stream().
                map(ColorAndSizeMapper::toEntity).
                toList());
        product.setGender(request.gender());
        product.setDescription(request.description());
        product.setPluses(request.pluses());
        product.setSpecifications(request.specifications());
        product.setUpdateAt(LocalDateTime.now());

        product.generateSlug();
        return product;
    }

    public static ProductResponseForGetSingleOne productToProductResponseForGetSingleOne(Product product, SubCategory subCategory, Seller seller, List<Plus> pluses, List<CommentResponse> comments){
        return new ProductResponseForGetSingleOne(
                product.getId(),
                product.getName(),
                product.getOriginalPrice(),
                product.getDiscountedPrice(),
                subCategory.getName(),
                product.getSellerId(),
                seller.getFullName(),
                product.getBrand(),
                product.getGender(),
                product.getDescription(),
                product.getSlug(),
                product.getSalesCount(),
                product.getTotalStock(),
                product.getRating(),
                product.getRatings(),
                pluses,
                product.getColorAndSizes(),
                product.getSpecifications(),
                product.getUpdateAt()!=null ?product.getUpdateAt():product.getCreateAt(),
                comments
        );
    }

    public static List<ProductResponseForGroupOfProduct> mapListOfProductToResponse(List<Product> products) {
        return products.stream()
                .map(product -> new ProductResponseForGroupOfProduct(
                        product.getColorAndSizes().getFirst().getImageUrls().getFirst(),
                        false,
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getRating(),
                        product.getRatings().size(),
                        product.getOriginalPrice(),
                        product.getDiscountedPrice()))
                .toList();
    }

    public static List<ProductResponseForGroupOfProduct> mapListOfProductToResponse(Map<Product, Boolean> products) {
        return products.entrySet().stream().map(product -> {
            Product productItem = product.getKey();
            return new ProductResponseForGroupOfProduct(
                    productItem.getColorAndSizes().getFirst().getImageUrls().getFirst(),
                    product.getValue(),
                    productItem.getId(),
                    productItem.getName(),
                    productItem.getDescription(),
                    productItem.getRating(),
                    productItem.getRatings().size(),
                    productItem.getOriginalPrice(),
                    productItem.getDiscountedPrice());
        }).toList();
    }
}

