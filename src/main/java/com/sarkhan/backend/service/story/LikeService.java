package com.sarkhan.backend.service.story;

import com.sarkhan.backend.dto.story.StoryResponseDTO;
import jakarta.security.auth.message.AuthException;

public interface LikeService {
    StoryResponseDTO toggleLikeOrDislike(Long storyId, String likeType) throws AuthException;
}
