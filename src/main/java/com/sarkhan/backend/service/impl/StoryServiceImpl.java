package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.model.story.Story;
import com.sarkhan.backend.repository.story.StoryRepository;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final CloudinaryService cloudinaryService;
    private final StoryRepository storyRepository;

    @Override
    public Story createStory(MultipartFile storyImage, MultipartFile adImage) throws IOException {

        String storyImageUrl = cloudinaryService.uploadFile(storyImage, "story_images");

        String adImageUrl = cloudinaryService.uploadFile(adImage, "product_ads");


        Story story = new Story();
        story.setStoryImageUrl(storyImageUrl);
        story.setAdImageUrl(adImageUrl);
        story.setCreatedAt(LocalDateTime.now().toString());


        return storyRepository.save(story);
    }

    @Override
    public List<Story> getAllStories() {
        return storyRepository.findAll();
    }

    @Override
    public String getAdImageUrl(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story is not found: " + storyId));
        return story.getAdImageUrl();
    }
}
