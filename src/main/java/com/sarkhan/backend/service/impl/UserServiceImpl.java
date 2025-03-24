package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.authorization.UserProfileRequest;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.enums.Gender;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public User updateUserProfile(UserProfileRequest userProfileRequest, String token) {
        Optional<User> user=userRepository.findByEmail(jwtService.extractEmail(token));
        if(user.isPresent()) {
            user.get().setBirthDate(userProfileRequest.getBirthDate());
            user.get().setEmail(userProfileRequest.getEmail());
            if (userProfileRequest.getGenderInt() == 0){
                user.get().setGender(Gender.MALE);
            }else if (userProfileRequest.getGenderInt() == 1) {
                user.get().setGender(Gender.FEMALE);
            }
            user.get().setCountryCode(userProfileRequest.getCountryCode());
            user.get().setPhoneNumber(userProfileRequest.getPhoneNumber());
            user.get().setNameAndSurname(userProfileRequest.getNameAndSurname());
userRepository.save(user.get());
        }

      return  user.get();
    }
}
