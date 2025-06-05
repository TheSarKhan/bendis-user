package com.sarkhan.backend.dto.product.items;

import java.util.List;

public record SubCategoryRequest(String name, Long categoryId, List<String> specification) {
}
