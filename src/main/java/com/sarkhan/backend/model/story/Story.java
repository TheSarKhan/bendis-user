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

    @Column(name = "seller_id")
    Long sellerId;

    @Column(name = "main_content_url", nullable = false)
    String mainContentUrl;

    @Column(name = "logo_url", nullable = false)
    String logoUrl;

    String description;

    @Column(name = "like_count")
    Long likeCount;

    @Column(name = "dislike_count")
    Long dislikeCount;

    @Column(name = "share_count")
    Long shareCount;

    @JdbcTypeCode(SqlTypes.JSON)
    List<Long> view;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @PrePersist
    private void init() {
        createdAt = updatedAt = LocalDateTime.now();
        likeCount = dislikeCount = shareCount = 0L;
    }
}
