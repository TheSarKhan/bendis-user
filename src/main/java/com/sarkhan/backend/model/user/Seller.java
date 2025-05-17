package com.sarkhan.backend.model.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Entity
@Table(name = "sellers")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String fullName;
    String brandName;
    String brandEmail;
    String brandVOEN;
    String fatherName;
    String finCode;
    String brandPhone;

}

