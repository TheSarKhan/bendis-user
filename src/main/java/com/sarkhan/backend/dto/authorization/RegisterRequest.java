package com.sarkhan.backend.dto.authorization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
     @NotBlank
     @Size(min = 3, max = 50)
     String fullName;
     @Email
     String email;
     @NotBlank
     @Size(min = 8)
     String password;
}
