package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.authorization.TokenResponse;
import com.sarkhan.backend.dto.user.UserResponse;
import com.sarkhan.backend.dto.user.UserUpdateRequest;
import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.user.User;
import jakarta.security.auth.message.AuthException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User save(User user);

    User getByEmail(String email);

    UserResponse getCurrentUser() throws AuthException;

    TokenResponse updateUser(UserUpdateRequest request, MultipartFile profileImage) throws AuthException, IOException;

    List<User> findUsersByRole(Role role);

}
