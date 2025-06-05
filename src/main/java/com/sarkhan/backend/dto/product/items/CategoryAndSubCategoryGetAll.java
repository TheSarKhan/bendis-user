package com.sarkhan.backend.dto.product.items;

import com.sarkhan.backend.model.product.items.Category;
import com.sarkhan.backend.model.product.items.SubCategory;

import java.util.List;

public record CategoryAndSubCategoryGetAll(List<Category> categories,
                                           List<SubCategory> subCategories) {
}
