package com.sarkhan.backend.model.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfile {
    String fincode;
    String phoneNumber;
    String userCode;
}
