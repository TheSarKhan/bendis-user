package com.sarkhan.backend.model.story;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    Long sellerId;

    @Column(nullable = false)
    String mainContentUrl;

    @Column(nullable = false)
    String logoUrl;

    String description;

    Long likeCount;

    Long dislikeCount;

    Long shareCount;

    @JdbcTypeCode(SqlTypes.JSON)
    List<Long> view;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @Column(nullable = false)
    LocalDateTime updatedAt;

    @PrePersist
    private void init() {
        createdAt = updatedAt = LocalDateTime.now();
        likeCount = dislikeCount = shareCount = 0L;
    }
}
