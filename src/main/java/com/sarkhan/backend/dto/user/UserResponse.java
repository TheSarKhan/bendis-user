package com.sarkhan.backend.dto.user;

import com.sarkhan.backend.model.enums.Gender;

public record UserResponse(String profileImage,
                           String fullName,
                           String email,
                           String phoneNumber,
                           Gender gender) {
}
