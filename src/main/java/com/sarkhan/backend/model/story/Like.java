package com.sarkhan.backend.model.story;

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

    @Column(nullable = false, name = "fk_story_id")
    String fkStoryId;

    @Column(nullable = false, name = "fk_user_id")
    String fkUserId;

    @Column(name = "like_status")
    String likeStatus;

}
