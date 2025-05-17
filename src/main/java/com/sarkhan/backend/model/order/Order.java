package com.sarkhan.backend.model.order;

import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.user.Seller;
import com.sarkhan.backend.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;
    @ManyToOne
    @JoinColumn(name = "seller_id",nullable = false)
    private Seller seller;
    private BigDecimal totalPrice;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDate orderDate;

}
