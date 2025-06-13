package com.sarkhan.backend.dto.product;

import com.sarkhan.backend.model.enums.Gender;
import com.sarkhan.backend.model.product.items.ColorAndSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductRequest(@NotBlank String name,
                             @Min(0) BigDecimal originalPrice,
                             @Min(0) BigDecimal discountedPrice,
                             Long subCategoryId,
                             @JdbcTypeCode(SqlTypes.JSON)
                             List<ColorAndSize> colorAndSizes,
                             Gender gender,
                             String description,
                             List<Long> pluses,
                             @JdbcTypeCode(SqlTypes.JSON)
                             Map<String, String> specifications) {
}