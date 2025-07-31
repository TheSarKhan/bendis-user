package com.sarkhan.backend.mapper.user;

import com.sarkhan.backend.dto.user.UserResponse;
import com.sarkhan.backend.dto.user.UserUpdateRequest;
import com.sarkhan.backend.model.user.User;

public class UserMapper {
    public static UserResponse mapUserToUserResponse(User user) {
        return new UserResponse(
                user.getProfileImg(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getGender());
    }

    public static User updateOldUserViaUserUpdateRequest(UserUpdateRequest request,User oldUser) {
        oldUser.setFullName(request.fullName());
        oldUser.setEmail(request.email());
        oldUser.setPhoneNumber(request.phoneNumber());
        oldUser.setGender(request.gender());
        return oldUser;
    }
}
