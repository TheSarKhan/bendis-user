package com.sarkhan.backend.controller.profile;

import com.sarkhan.backend.dto.profile.UserProfileDto;
import com.sarkhan.backend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userProfileService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserProfileDto>> getAllUsers() {
        return ResponseEntity.ok(userProfileService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserProfileDto> createUserProfile(@RequestBody UserProfileDto userProfileDto) {
        return ResponseEntity.ok(userProfileService.createUserProfile(userProfileDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @PathVariable Long id,
            @RequestBody UserProfileDto userProfileDto) {
        return ResponseEntity.ok(userProfileService.updateUserProfile(id, userProfileDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Long id) {
        userProfileService.deleteUserProfile(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadProfileImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = userProfileService.uploadProfileImage(id, file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("File upload failed: " + e.getMessage());
        }
    }
}
