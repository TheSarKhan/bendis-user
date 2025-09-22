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
    @Column(name = "id")
    Long id;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "brand_name")
    String brandName;

    @Column(name = "brand_email")
    String brandEmail;

    @Column(name = "brand_voen")
    String brandVOEN;

    @Column(name = "father_name")
    String fatherName;

    @Column(name = "fin_code")
    String finCode;

    @Column(name = "brand_phone")
    String brandPhone;
}
