package com.sarkhan.backend.model.order;

import com.sarkhan.backend.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "cart_id")
    private Long cartId;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;
    @Column(name = "order_date")
    private LocalDate orderDate;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private List<OrderItem> orderItemList;

    @PrePersist
    public void setDefault() {
        if (orderStatus == null) {
            this.orderStatus = OrderStatus.PENDING;
        }
        orderDate = LocalDate.now();
        updatedAt = LocalDateTime.now();
    }
}
