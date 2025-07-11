package com.sarkhan.backend.model.order;

import com.sarkhan.backend.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "order_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;
    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;
    LocalDate changedAt;

    @PrePersist
    public void setDefault() {
        changedAt = LocalDate.now();
        orderStatus = OrderStatus.PENDING;
    }

}
