package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.authorization.UserProfileRequest;
import com.sarkhan.backend.model.user.User;

public interface UserService {
    User save(User user);

    User updateUserProfile(UserProfileRequest userProfileRequest, String token);

    User getByEmail(String email);
}
