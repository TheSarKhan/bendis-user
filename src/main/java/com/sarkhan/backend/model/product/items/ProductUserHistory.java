package com.sarkhan.backend.model.product.items;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "product_user_histories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUserHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "sub_category_id")
    Long subCategoryId;
}
