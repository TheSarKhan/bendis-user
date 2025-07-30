package com.sarkhan.backend.model.comment;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(nullable = false)
    Long userId;

    @Column(nullable = false)
    String userName;

    @Column(nullable = false)
    Long productId;

    @Column(nullable = false)
    String content;

    Long usefulCount;

    List<Long> useful;

    @Column(nullable = false)
    double rating;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        usefulCount = 0L;
    }
}
