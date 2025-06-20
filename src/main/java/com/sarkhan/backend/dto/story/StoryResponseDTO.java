package com.sarkhan.backend.dto.story;

import com.sarkhan.backend.model.story.Story;
import com.sarkhan.backend.model.user.Seller;

public record StoryResponseDTO(Story story, Seller seller) {
}
