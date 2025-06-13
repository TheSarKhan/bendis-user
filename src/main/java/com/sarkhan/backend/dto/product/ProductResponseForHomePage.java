package com.sarkhan.backend.dto.product;

import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.Category;
import com.sarkhan.backend.model.product.items.SubCategory;

import java.util.List;

public record ProductResponseForHomePage(List<Product> famousProducts,
                                         List<Product> discountedProducts,
                                         List<Product> mostFavoriteProducts,
                                         List<Product> flashProducts,
                                         List<Product> recommendedProducts,
                                         List<Category> categories,
                                         List<SubCategory> subCategories) {
}
