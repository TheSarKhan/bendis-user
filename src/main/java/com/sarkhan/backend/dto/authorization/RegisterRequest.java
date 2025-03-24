package com.sarkhan.backend.dto.authorization;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

     String nameAndSurname;
     String email;
     String password;
     byte genderInt;
}
