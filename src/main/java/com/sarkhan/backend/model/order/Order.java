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
    private Long orderId;
    private Long cartId;
    private BigDecimal totalPrice;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDate orderDate;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER)
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
