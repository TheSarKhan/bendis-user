package com.sarkhan.backend.model.order;

import com.sarkhan.backend.model.address.Address;
import com.sarkhan.backend.model.cart.Cart;
import com.sarkhan.backend.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
    private Double totalPrice;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;

    @PrePersist
    public void setDefaultStatus() {
        if (orderStatus==null) {
            this.orderStatus = OrderStatus.PENDING;
        }
    }
}
