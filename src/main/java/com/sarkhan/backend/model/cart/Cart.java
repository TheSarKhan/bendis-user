package com.sarkhan.backend.model.cart;


import com.sarkhan.backend.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "carts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "user_id")
    Long userId;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    List<CartItem> cartItems;
    @Column(name = "created_at")
    LocalDate createdAt;

    @PrePersist
    void setDefault() {
        createdAt = LocalDate.now();
    }

}
