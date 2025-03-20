package com.sarkhan.backend.model.user;

import com.sarkhan.backend.model.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    private String userNamaAndSurname;
    private String phoneNumber;
    private String fin;
    private String customerCode;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String profileImageUrl; // Faylın URL-i saxlanacaq

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
