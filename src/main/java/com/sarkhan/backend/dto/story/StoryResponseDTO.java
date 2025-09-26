package com.sarkhan.backend.dto.story;

import com.sarkhan.backend.model.seller.Seller;
import com.sarkhan.backend.model.story.Story;

public record StoryResponseDTO(Story story, Seller seller) {
}
