package com.sarkhan.backend.dto.product;

import com.sarkhan.backend.model.product.items.Color;
import com.sarkhan.backend.model.product.items.Comment;
import com.sarkhan.backend.model.product.items.Plus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    String name;
    Double price;
    String category;
    Double stock;
    @JdbcTypeCode(SqlTypes.JSON)
    List<Color> colors;
    @JdbcTypeCode(SqlTypes.JSON)
    List<String>descriptions;
     List<Long>pluses;
    @JdbcTypeCode(SqlTypes.JSON)
    HashMap<String, String> specifications;
}
