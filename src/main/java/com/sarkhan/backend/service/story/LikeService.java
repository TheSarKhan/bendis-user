package com.sarkhan.backend.service.story;

import com.sarkhan.backend.dto.story.StoryResponseDTO;
import com.sarkhan.backend.model.enums.LikeType;
import jakarta.security.auth.message.AuthException;

public interface LikeService {
    StoryResponseDTO toggleLikeOrDislike(Long storyId, LikeType likeType) throws AuthException;
}
