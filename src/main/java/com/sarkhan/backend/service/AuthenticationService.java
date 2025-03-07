package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.UserDto;
import com.sarkhan.backend.dto.UserRequest;
import com.sarkhan.backend.dto.UserResponse;
import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserCacheService userCacheService;

    public UserResponse save(UserDto dto) {
        System.out.println("Save metoduna giris edildi");
        User user = User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .nameSoname(dto.getNameSoname())
                .role(Role.USER)
                .build();
        System.out.println("User entity hazir");
        repository.save(user);
        System.out.println("Databaseye melumat getdi");
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        redisTemplate.opsForValue().set(
                "access_token:" + user.getEmail(), accessToken, 24, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(
                "refresh_token:" + user.getEmail(), refreshToken, 7, TimeUnit.DAYS);

        System.out.println("Save isledi");
        System.out.println("Access Token: " + accessToken);
        System.out.println("Refresh Token: " + refreshToken);
        return UserResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserResponse auth(UserRequest dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        User user = userCacheService.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

        String accessToken = (String) redisTemplate.opsForValue().get("access_token:" + dto.getEmail());
        String refreshToken = (String) redisTemplate.opsForValue().get("refresh_token:" + dto.getEmail());

        if (accessToken == null || refreshToken == null) {
            accessToken = jwtService.generateToken(user);
            refreshToken = jwtService.generateRefreshToken(user);
            redisTemplate.opsForValue().set(
                    "access_token:" + dto.getEmail(), accessToken, 24, TimeUnit.HOURS);
            redisTemplate.opsForValue().set(
                    "refresh_token:" + dto.getEmail(), refreshToken, 7, TimeUnit.DAYS);
        }

        System.out.println("Access Token: " + accessToken);
        System.out.println("Refresh Token: " + refreshToken);
        return UserResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}