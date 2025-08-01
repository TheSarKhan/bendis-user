package com.sarkhan.backend.dto.header;

import com.sarkhan.backend.dto.user.UserResponse;
import com.sarkhan.backend.model.product.items.Category;
import com.sarkhan.backend.model.product.items.SubCategory;

import java.util.List;

public record HeaderResponse(UserResponse user,
                             List<Category> categories,
                             List<SubCategory> subCategories) {
}
