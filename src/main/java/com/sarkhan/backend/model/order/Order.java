package com.sarkhan.backend.model.order;

import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.user.Seller;
import com.sarkhan.backend.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

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

    @Column(name = "user_id", nullable = false)
    private Long userId;  // User entity yerine sadece ID tut

    @Column(name = "product_id", nullable = false)
    private Long productId;  // Aynı şekilde Product ID

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;  // Seller entity yerine sadece ID tut

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDate orderDate;
}
