package com.sarkhan.backend.model.product;

import com.sarkhan.backend.model.product.items.Color;
import com.sarkhan.backend.model.product.items.Comment;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.List;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    public void generateSlug() {
        this.slug = this.name.toLowerCase()
                .replace(" ", "-")
                .replaceAll("[^a-z0-9-]", "");
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    String name;
    Double price;
    String category;
    Double rating;
    String brand;
    String slug;
    @JdbcTypeCode(SqlTypes.JSON)
    List<Color> colors;
    @JdbcTypeCode(SqlTypes.JSON)
    List<String> descriptions;
    @JdbcTypeCode(SqlTypes.JSON)
    List<Comment> comments;
    @JdbcTypeCode(SqlTypes.JSON)
    List<Long> pluses;
    @JdbcTypeCode(SqlTypes.JSON)
    HashMap<String, String> specifications;
}

