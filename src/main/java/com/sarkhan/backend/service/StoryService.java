package com.sarkhan.backend.service;


import com.sarkhan.backend.model.story.Story;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StoryService {

    Story createStory(MultipartFile storyImage, MultipartFile adImage) throws IOException;
    List<Story> getAllStories();
    String getAdImageUrl(Long storyId);
}
