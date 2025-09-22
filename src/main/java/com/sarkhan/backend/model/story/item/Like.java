package com.sarkhan.backend.model.story.item;

import com.sarkhan.backend.model.enums.LikeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "likes")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(name = "story_id", nullable = false)
    Long storyId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "like_type", nullable = false)
    LikeType likeType;
}
