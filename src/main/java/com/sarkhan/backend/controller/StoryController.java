package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.story.StoryResponseDTO;
import com.sarkhan.backend.model.story.Story;
import com.sarkhan.backend.service.impl.StoryServiceImpl;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stories")
@RequiredArgsConstructor
public class StoryController {
    private final StoryServiceImpl service;

    @PostMapping
    public ResponseEntity<StoryResponseDTO> createStory(@RequestParam Long sellerId,
                                                        @RequestParam String description,
                                                        MultipartFile mainContent,
                                                        MultipartFile logo) throws IOException {
        return ResponseEntity.ok(service.add(sellerId, description, mainContent, logo));
    }

    @GetMapping
    public ResponseEntity<List<Story>> getAllStories() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryResponseDTO> getByIdAndAddView(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByIdAndAddView(id));
    }

    @PatchMapping("/{storyId}/{likeType}")
    public ResponseEntity<StoryResponseDTO> toggleLikeOrDislike(@PathVariable Long storyId,
                                                                @PathVariable String likeType) throws AuthException {
        return ResponseEntity.ok(service.toggleLikeOrDislike(storyId, likeType));
    }

    @PutMapping
    public ResponseEntity<StoryResponseDTO> update(@RequestParam Long sellerId,
                                                   @RequestParam Long id,
                                                   @RequestParam String description,
                                                   MultipartFile mainContent,
                                                   MultipartFile logo) throws AuthException, IOException {
        return ResponseEntity.ok(service.update(sellerId, id, description, mainContent, logo));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam Long sellerId,
                                       @RequestParam Long id) throws AuthException {
        service.delete(sellerId, id);
        return ResponseEntity.ok().build();
    }
}
