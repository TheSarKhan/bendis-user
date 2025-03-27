package com.sarkhan.backend.model.product;

import com.sarkhan.backend.dto.comment.CommentResponse;
import com.sarkhan.backend.model.product.items.Color;


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
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    String name;
    Double price;
    String category;
    Double rating;
    @JdbcTypeCode(SqlTypes.JSON)
    List<Color>colors;
    @JdbcTypeCode(SqlTypes.JSON)
    List<String>descriptions;
    @JdbcTypeCode(SqlTypes.JSON)
    List<CommentResponse>comments;
    @JdbcTypeCode(SqlTypes.JSON)
    List<Long>pluses;
    @JdbcTypeCode(SqlTypes.JSON)
    HashMap<String, String> specifications;
}
