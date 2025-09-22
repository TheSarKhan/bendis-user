package com.sarkhan.backend.model.product;

import com.sarkhan.backend.model.enums.Gender;
import com.sarkhan.backend.model.product.items.ColorAndSize;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(name = "original_price", nullable = false)
    BigDecimal originalPrice;

    @Column(name = "discounted_price")
    BigDecimal discountedPrice;

    @Column(name = "sub_category_id")
    Long subCategoryId;

    @Column(name = "seller_id", nullable = false)
    Long sellerId;

    String brand;

    @Enumerated(value = EnumType.STRING)
    Gender gender;

    @Column(nullable = false)
    String description;

    String slug;

    @Column(name = "sales_count")
    Integer salesCount;

    @Column(name = "total_stock")
    Long totalStock;

    Double rating;

    @JdbcTypeCode(SqlTypes.JSON)
    Map<Long, Double> ratings;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "favorite_count")
    Long favoriteCount;

    @JdbcTypeCode(SqlTypes.JSON)
    List<Long> pluses;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "color_and_sizes")
    List<ColorAndSize> colorAndSizes;

    @JdbcTypeCode(SqlTypes.JSON)
    Map<String, String> specifications;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updateAt;

    @PrePersist
    public void init() {
        createdAt = LocalDateTime.now();
        rating = 0.0;
        ratings = new HashMap<>();
        salesCount = 0;
        favoriteCount = 0L;
        totalStock = colorAndSizes.stream().
                mapToLong(color -> {
                    if (color.getSizeStockMap() == null) return color.getStock();
                    return color.getSizeStockMap().
                            values().
                            stream().
                            mapToLong(Long::longValue).
                            sum();
                })
                .sum();
        generateSlug();
    }

    public void generateSlug() {
        this.slug = this.name
                .toLowerCase()
                .replace("ç", "ch")
                .replace("ş", "sh")
                .replace("ğ", "gh")
                .replace("ü", "u")
                .replace("ö", "o")
                .replace("ı", "i")
                .replace("ə", "e")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }

}