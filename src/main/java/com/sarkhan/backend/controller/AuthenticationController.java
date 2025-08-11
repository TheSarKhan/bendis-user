package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.authorization.LoginRequest;
import com.sarkhan.backend.dto.authorization.RegisterRequest;
import com.sarkhan.backend.dto.authorization.TokenResponse;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.redis.RedisService;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.AuthenticationService;
import com.sarkhan.backend.service.impl.CustomOAuth2UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final CustomOAuth2UserServiceImpl customOAuth2UserServiceImpl;

    private final AuthenticationService authenticationService;

    private final UserRepository userRepository;

    private final RedisService redisService;

    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody @Valid RegisterRequest dto) {
        TokenResponse tokenResponse = authenticationService.register(dto);
        Optional<User> user = userRepository.findByEmail(jwtService.extractEmail(tokenResponse.getAccessToken()));
        if (user.isPresent()) {
            user.get().setUserCode("BD" + 10000000 + user.get().getId());
            userRepository.save(user.get());
        }

        return ResponseEntity.status(200).body(tokenResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authenticationService.login(request);
        return ResponseEntity.status(200).body(tokenResponse);
    }


    @PostMapping("/google-login")
    @Operation(summary = "Google linki istifadə edərək asan login üçün endpoint")
    public ResponseEntity<TokenResponse> googleLogin(@RequestBody Map<String, String> body) {
        String idToken = body.get("id_token");
        try {
            TokenResponse response = customOAuth2UserServiceImpl.processGoogleLogin(idToken);
            return ResponseEntity.ok(response);
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/oauth2-failure")
    public ResponseEntity<String> oauth2Failure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OAuth2 login failed.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
         String email = jwtService.extractEmail(refreshToken);

        if (email.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        String storedRefreshToken = redisService.getRefreshToken(email);
        System.out.println(storedRefreshToken +"Stored Refresh Token");
        System.out.println(refreshToken + "Current Refresh token");
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            return ResponseEntity.status(401).body("Refresh token mismatch or expired");
        }

        String newAccessToken = jwtService.generateAccessToken(email,null);

        String newRefreshToken = jwtService.generateRefreshToken(email);
        redisService.deleteRefreshToken(email);
        redisService.saveRefreshToken(email, newRefreshToken, 7);

        return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken));
    }

}
