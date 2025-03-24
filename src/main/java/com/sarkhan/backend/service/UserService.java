package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.authorization.UserProfileRequest;
import com.sarkhan.backend.model.user.User;

public interface UserService {
    User updateUserProfile(UserProfileRequest userProfileRequest, String token);
}
