package com.sarkhan.backend.dto.user;

import com.sarkhan.backend.model.enums.Gender;

public record UserUpdateRequest(String fullName,
                                String email,
                                String phoneNumber,
                                Gender gender) {
}
