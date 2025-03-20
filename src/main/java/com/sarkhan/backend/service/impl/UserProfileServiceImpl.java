package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.profile.UserProfileDto;
import com.sarkhan.backend.model.enums.Gender;
import com.sarkhan.backend.model.user.UserProfile;
import com.sarkhan.backend.repository.user.UserProfileRepository;
import com.sarkhan.backend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private static final String UPLOAD_DIR = "uploads/";

    @Override
    public UserProfileDto getUserById(Long userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toDto(userProfile);
    }

    @Override
    public List<UserProfileDto> getAllUsers() {
        return userProfileRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserProfileDto createUserProfile(UserProfileDto userProfileDto) {
        UserProfile userProfile = toEntity(userProfileDto);
        userProfile = userProfileRepository.save(userProfile);
        return toDto(userProfile);
    }

    @Override
    public UserProfileDto updateUserProfile(Long userId, UserProfileDto userProfileDto) {
        UserProfile existingUser = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUserNamaAndSurname(userProfileDto.getUserNamaAndSurname());
        existingUser.setPhoneNumber(userProfileDto.getPhoneNumber());
        existingUser.setFin(userProfileDto.getFin());
        existingUser.setCustomerCode(userProfileDto.getCustomerCode());
        existingUser.setProfileImageUrl(userProfileDto.getProfileImageUrl());

        if (userProfileDto.getGender() != null) {
            existingUser.setGender(Gender.valueOf(userProfileDto.getGender()));
        }

        UserProfile updatedUser = userProfileRepository.save(existingUser);
        return toDto(updatedUser);
    }

    @Override
    public void deleteUserProfile(Long userId) {
        userProfileRepository.deleteById(userId);
    }

    @Override
    public String uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Unikal şəkil adı yarat
        String fileName = userId + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        // Qovluq mövcud deyilsə, yarad
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Faylı serverdə yadda saxla
        Files.write(filePath, file.getBytes());

        // Yeni şəkil URL-sini user profiline əlavə et
        String fileUrl = "/uploads/" + fileName;
        userProfile.setProfileImageUrl(fileUrl);
        userProfileRepository.save(userProfile);

        return fileUrl;
    }

    // Entity to DTO çevirən metod
    private UserProfileDto toDto(UserProfile userProfile) {
        return UserProfileDto.builder()
                .userId(userProfile.getUserId())
                .userNamaAndSurname(userProfile.getUserNamaAndSurname())
                .phoneNumber(userProfile.getPhoneNumber())
                .fin(userProfile.getFin())
                .customerCode(userProfile.getCustomerCode())
                .gender(userProfile.getGender() != null ? userProfile.getGender().name() : null)
                .profileImageUrl(userProfile.getProfileImageUrl())
                .createdAt(userProfile.getCreatedAt())
                .updatedAt(userProfile.getUpdatedAt())
                .build();
    }

    // DTO to Entity çevirən metod
    private UserProfile toEntity(UserProfileDto userProfileDto) {
        return UserProfile.builder()
                .userNamaAndSurname(userProfileDto.getUserNamaAndSurname())
                .phoneNumber(userProfileDto.getPhoneNumber())
                .fin(userProfileDto.getFin())
                .customerCode(userProfileDto.getCustomerCode())
                .gender(userProfileDto.getGender() != null ? Gender.valueOf(userProfileDto.getGender()) : null)
                .profileImageUrl(userProfileDto.getProfileImageUrl())
                .build();
    }
}
