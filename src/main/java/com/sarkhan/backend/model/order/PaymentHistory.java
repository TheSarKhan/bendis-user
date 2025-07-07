package com.sarkhan.backend.model.order;

import com.sarkhan.backend.model.enums.PaymentMethod;
import com.sarkhan.backend.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String cardLastFourDigits;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String transactionId;
    private String responseMessage;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private Long userId;

}
