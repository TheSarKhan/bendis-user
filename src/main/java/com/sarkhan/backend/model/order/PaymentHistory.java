package com.sarkhan.backend.model.order;

import com.sarkhan.backend.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Entity
@Table(name = "payment_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "card_last_four_digits")
    String cardLastFourDigits;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    PaymentStatus paymentStatus;
    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;
    @Column(name = "paid_date")
    LocalDate paidDate;

    @PrePersist
    public void setDefault() {
        paidDate = LocalDate.now();
        paymentStatus = PaymentStatus.PENDING;
    }

}
