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

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "user_name", nullable = false)
    String userName;

    @Column(name = "product_id", nullable = false)
    Long productId;

    @Column(nullable = false)
    String content;

    @Column(name = "useful_count")
    Long usefulCount;

    List<Long> useful;

    @Column(nullable = false)
    double rating;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        usefulCount = 0L;
    }
}
