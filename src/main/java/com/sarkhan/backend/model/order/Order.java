package com.sarkhan.backend.model.order;

import com.sarkhan.backend.model.cart.Cart;
import com.sarkhan.backend.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
    private BigDecimal totalPrice;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;
    LocalDate orderDate;
    @Column(name = "user_id", nullable = false)
    private Long userId;


    @PrePersist
    public void setDefault() {
        if (orderStatus==null) {
            this.orderStatus = OrderStatus.PENDING;
        }
        orderDate=LocalDate.now();
    }
}
