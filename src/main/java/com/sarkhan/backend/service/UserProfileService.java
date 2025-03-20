package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.profile.UserProfileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserProfileService {
    UserProfileDto getUserById(Long userId);

    List<UserProfileDto> getAllUsers();

    UserProfileDto createUserProfile(UserProfileDto userProfileDto);

    UserProfileDto updateUserProfile(Long userId, UserProfileDto userProfileDto);

    void deleteUserProfile(Long userId);

    String uploadProfileImage(Long userId, MultipartFile file) throws IOException;
}
