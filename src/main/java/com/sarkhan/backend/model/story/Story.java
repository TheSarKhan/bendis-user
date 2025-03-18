package com.sarkhan.backend.model.story;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String storyImageUrl;

    @Column(nullable = false)
    private String adImageUrl;

    @Column(nullable = false)
    private String createdAt;
}
