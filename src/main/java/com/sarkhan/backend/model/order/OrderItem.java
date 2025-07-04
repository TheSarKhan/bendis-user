package com.sarkhan.backend.model.order;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ORM əlaqə çıxarılır, yerinə yalnız orderId saxlanılır
    private Long orderId;

    // ORM əlaqə çıxarılır, yerinə yalnız productId saxlanılır
    private Long productId;

    private Long userId;

    private Integer quantity;
}


