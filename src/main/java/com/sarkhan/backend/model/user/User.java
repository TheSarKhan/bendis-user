package com.sarkhan.backend.model.user;

import com.sarkhan.backend.model.enums.Gender;
import com.sarkhan.backend.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    Long id;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "google_id")
    String googleId;

    @Column(name = "profile_img")
    String profileImg;

    @Column(name = "email", unique = true)
    String email;

    @Column(name = "refresh_token")
    String refreshToken;

    @Column(name = "password")
    String password;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(name = "user_code")
    String userCode;

    @Column(name = "country_code")
    String countryCode;

    @Column(name = "phone_number")
    String phoneNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "birth_date")
    BirthDate birthDate;

    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    Role role;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "seller")
    Seller seller;
}
