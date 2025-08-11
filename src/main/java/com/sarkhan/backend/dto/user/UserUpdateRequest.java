package com.sarkhan.backend.dto.user;

import com.sarkhan.backend.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(@NotBlank @Size(min = 3, max = 50) String fullName,
                                @Email String email,
                                @NotBlank String phoneNumber,
                                Gender gender) {
}
