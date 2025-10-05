package com.sarkhan.backend.model.admin;

import com.sarkhan.backend.model.enums.Gender;
import com.sarkhan.backend.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "admins")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String fullName;

    @Column(unique = true)
    String email;

    String password;

    @Enumerated(EnumType.STRING)
    Gender gender;

    String phoneNumber;

    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    Role role;
}

