package com.sarkhan.backend.dto.product;

import com.sarkhan.backend.model.enums.Gender;
import com.sarkhan.backend.model.product.items.Color;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductRequest(
        @NotBlank String name,
        @Min(0) BigDecimal originalPrice,
        @Min(0) BigDecimal discountedPrice,
        Long subCategoryId,
        List<Color> colors,  // @JdbcTypeCode annotation silindi
        Gender gender,
        String description,
        List<Long> pluses,  // @JdbcTypeCode annotation silindi
        Map<String, String> specifications  // @JdbcTypeCode annotation silindi
) {}
//{
//  eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHJpbmczQGdtYWlsLmNvbSIsImlhdCI6MTc1MTYyODg3OCwiZXhwIjoxNzUxNjMyNDc4fQ.nff6bhqYlgCy7V49DGy5nvq2YyS4ixEw-N2p1kymQWA",
//  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHJpbmczQGdtYWlsLmNvbSIsImlhdCI6MTc1MTYyODg3NSwiZXhwIjoxNzUyMjMzNjc1fQ.tAv485qaCNBLf3tHKgKe8_b7pzIYxc8LJY9zNiaTEqo"
//}