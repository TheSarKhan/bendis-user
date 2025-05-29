package com.sarkhan.backend.model.product;

import com.sarkhan.backend.model.enums.Gender;
import com.sarkhan.backend.model.product.items.Color;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(name = "original_price", nullable = false)
    BigDecimal originalPrice;

    @Column(name = "discount_price")
    BigDecimal discountedPrice;

    @Column(name = "sub_category_id", nullable = false)
    Long subCategoryId;

    @Column(name = "seller_id", nullable = false)
    Long sellerId;

    String brand;

    @Enumerated(value = EnumType.STRING)
    Gender gender;

    @Column(nullable = false)
    String description;

    String slug;

    Double rating;

    @JdbcTypeCode(SqlTypes.JSON)
    Map<Long, Double> ratings;

    @JdbcTypeCode(SqlTypes.JSON)
    List<Long> comments;

    @JdbcTypeCode(SqlTypes.JSON)
    Set<Long> favorites;

    @JdbcTypeCode(SqlTypes.JSON)
    List<Long> pluses;

    @JdbcTypeCode(SqlTypes.JSON)
    List<Color> colors;

    @JdbcTypeCode(SqlTypes.JSON)
    Map<String, List<String>> specifications;

    @Column(name = "create_at")
    LocalDateTime createAt;

    @Column(name = "update_at")
    LocalDateTime updateAt;

    @PrePersist
    public void init() {
        createAt = LocalDateTime.now();
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
                .replace("İ", "i")
                .replace("Ç", "ch")
                .replace("Ş", "sh")
                .replace("Ğ", "gh")
                .replace("Ü", "u")
                .replace("Ö", "o")
                .replace("Ə", "e")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }

}