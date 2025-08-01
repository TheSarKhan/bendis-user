package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.authorization.TokenResponse;
import com.sarkhan.backend.dto.user.UserResponse;
import com.sarkhan.backend.dto.user.UserUpdateRequest;
import com.sarkhan.backend.mapper.user.UserMapper;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.AuthenticationService;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.UserService;
import com.sarkhan.backend.service.impl.product.util.UserUtil;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final CloudinaryService cloudinaryService;

    private final AuthenticationService authenticationService;

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        log.info("Someone try to create User.");
        return userRepository.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("Cannot find user with " + email + " email.");
            return new NoSuchElementException("Cannot find user with " + email + " email.");
        });
    }

    @Override
    public UserResponse getCurrentUser() throws AuthException {
        log.info("Someone try to get current user.");
        return UserMapper.mapUserToUserResponse(UserUtil.getCurrentUser(this, log));
    }

    @Override
    public TokenResponse updateUser(UserUpdateRequest request, MultipartFile profileImage) throws AuthException, IOException {
        log.info("Someone try to update user.");
        var oldUser = UserUtil.getCurrentUser(this, log);
        var user = UserMapper.updateOldUserViaUserUpdateRequest(request, oldUser);
        if (user.getProfileImg()!=null)
            cloudinaryService.deleteFile(user.getProfileImg());
        if (profileImage != null && !profileImage.isEmpty()) {
            String uploadedImageUrl = cloudinaryService.uploadFile(profileImage, "userProfileImages").getUrl();
            user.setProfileImg(uploadedImageUrl);
        }
        userRepository.save(user);
        log.info("User successfully updated. User : " + user.getFullName());
        return authenticationService.getTokensAfterUpdateProfile(user.getEmail());
    }
}
