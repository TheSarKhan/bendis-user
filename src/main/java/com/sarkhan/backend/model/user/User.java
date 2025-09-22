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
    Long id;
    String fullName;

    String googleId;

    String profileImg;

    @Column( unique = true)
    String email;

    String refreshToken;
    String password;

    @Enumerated(EnumType.STRING)
    Gender gender;

    String userCode;

    String countryCode;

    String phoneNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    BirthDate birthDate;

    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    Role role;

    @JdbcTypeCode(SqlTypes.JSON)
    Seller seller;
}
