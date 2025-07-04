package com.sarkhan.backend.repository.story;

import com.sarkhan.backend.model.story.item.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> getByStoryIdAndUserId(Long storyId, Long userId);
}
