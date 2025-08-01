package com.sarkhan.backend.dto.product;

import com.sarkhan.backend.model.product.Product;

import java.util.List;

public record ProductResponseForHomePage(List<Product> famousProducts,
                                         List<Product> discountedProducts,
                                         List<Product> mostFavoriteProducts,
                                         List<Product> flashProducts,
                                         List<Product> recommendedProducts) {
}
