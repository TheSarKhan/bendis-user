package com.sarkhan.backend.model.story;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "advertisements")
public class Advertisement {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String link;

    private String description;

    private Set<Long> view;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    private void init() {
        id = UUID.randomUUID();
        view = Set.of();
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    private void update() {
        updatedAt = Instant.now();
    }
}
