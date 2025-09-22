package com.sarkhan.backend.model.cart;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cart_items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    Cart cart;
    @Column(name = "product_id")
    Long productId;
    Integer quantity;
    String color;
    @Column(name = "total_price")
    BigDecimal totalPrice;

}