package com.sarkhan.backend.dto.authorization;

import com.sarkhan.backend.model.user.BirthDate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileRequest {
    String nameAndSurname;
    String email;
    byte genderInt;
    String countryCode;
    String phoneNumber;
    BirthDate birthDate;

}
