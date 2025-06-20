package com.sarkhan.backend.dto.product;

import com.sarkhan.backend.model.enums.Color;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.Category;
import com.sarkhan.backend.model.product.items.SubCategory;

import java.util.List;

public record ProductResponseForSelectedSubCategoryAndComplexFilter(List<Product> products,
                                                                    List<Category> categories,
                                                                    List<SubCategory> subCategories,
                                                                    List<String> specifications,
                                                                    List<Color> colors,
                                                                    List<String> sizes,
                                                                    ProductFilterRequest selectedFilters) {
}
