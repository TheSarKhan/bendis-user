package com.sarkhan.backend.model.product.items;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_favorite_products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFavoriteProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    Long userId;

    Long productId;
}
