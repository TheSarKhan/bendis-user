package com.sarkhan.backend.controller;

import com.sarkhan.backend.model.story.Story;
import com.sarkhan.backend.service.impl.StoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryServiceImpl storyService;

    // Yeni story yaratmaq üçün
    @PostMapping
    public ResponseEntity<Story> createStory(
            @RequestParam("storyImage") MultipartFile storyImage,
            @RequestParam("adImage") MultipartFile adImage) throws IOException {
        Story story = storyService.createStory(storyImage, adImage);
        return ResponseEntity.ok(story);
    }

    // Bütün storyləri çəkmək üçün
    @GetMapping("/get-all")
    public ResponseEntity<List<Story>> getAllStories() {
        List<Story> stories = storyService.getAllStories();
        return ResponseEntity.ok(stories);
    }

    // Reklam şəkli üçün URL çəkmək
    @GetMapping("/id/{id}")
    public ResponseEntity<String> getAdImageUrl(@PathVariable Long id) {
        String adImageUrl = storyService.getAdImageUrl(id);
        return ResponseEntity.ok(adImageUrl);
    }

    @PostMapping("/like/story-id/{story-id}")
    public ResponseEntity<String> addAndRemoveLike(@PathVariable("story-id") String storyId,
                                                   @RequestParam String userId) {
        storyService.addAndRemoveLike(storyId, userId);

        return ResponseEntity.ok("Success");
    }

    @GetMapping("/get-like/story-id/{story-id}")
    public Optional<Long> getLike(@PathVariable("story-id") String storyId) {
        return storyService.getLike(storyId);
    }

}
