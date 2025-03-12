package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.LoginRequest;
import com.sarkhan.backend.dto.RegisterRequest;
import com.sarkhan.backend.dto.TokenResponse;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.redis.RedisService;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    @Override
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email zaten kayıtlı!");
        }
        LocalDateTime now = LocalDateTime.now();
        String refreshToken = jwtService.generateRefreshToken(request.getEmail());
        User user = new User();
        user.setNameAndSurname(request.getNameAndSurname());
        user.setEmail(request.getEmail());
         user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(request.getEmail(), null); // Claims kısmı null olabilir

        redisService.saveTokenToRedis(accessToken, request.getEmail());
        String savedToken = redisService.getTokenFromRedis(request.getEmail());
        if (savedToken != null) {
            System.out.println("Redis'ten alınan token: " + savedToken);
        } else {
            System.out.println("Token Redis'ten alınamadı");
        }
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    @Override
    public TokenResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with username: " + request.getEmail());
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail(), null);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
