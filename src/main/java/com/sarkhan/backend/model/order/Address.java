package com.sarkhan.backend.model.order;

import com.sarkhan.backend.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "fin_code")
    String finCode;
    String region;
    String street;
    String city;
    @Column(name = "postal_code")
    String postalCode;
    Long userId;

}
