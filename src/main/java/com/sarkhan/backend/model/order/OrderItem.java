package com.sarkhan.backend.model.order;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    Order order;
    @Column(name = "product_id")
    Long productId;
    @Column(name = "seller_id")
    Long sellerId;
    Integer quantity;
    String color;
    String size;
    @Column(name = "total_price")
    BigDecimal totalPrice;
    @Column(name = "delivery_fee")
    BigDecimal deliveryFee;
}
