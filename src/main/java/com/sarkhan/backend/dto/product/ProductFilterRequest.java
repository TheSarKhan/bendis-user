package com.sarkhan.backend.dto.product;

import com.sarkhan.backend.model.enums.Color;
import com.sarkhan.backend.model.enums.Gender;

import java.util.List;
import java.util.Map;

public record ProductFilterRequest(Long subCategoryId,
                                   List<Color> colors,
                                   List<String> sizes,
                                   Double rating,
                                   Double minPrice,
                                   Double maxPrice,
                                   Gender gender,
                                   Map<String, List<String>> specifications) {
}