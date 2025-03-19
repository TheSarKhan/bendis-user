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


    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
private final RedisService redisService;
    private final CustomOAuth2UserServiceImpl customOAuth2UserServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest dto) {
        System.out.println("Kontroller isledi");
        return ResponseEntity.ok(authenticationService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
TokenResponse tokenResponse = authenticationService.login(request);
if (request.getEmail() == null || request.getPassword() == null) {
    ResponseEntity.status(400).body("Email or Password is null");
}
if (user.isEmpty()) {
    ResponseEntity.status(400).body("Email does not exist");
}
         if (!Objects.equals(request.getPassword(), user.get().getPassword())) {
             ResponseEntity.status(400).body("Passwords do not match");
         }
return ResponseEntity.status(200).body(tokenResponse);
    }


    @PostMapping("/google-login")
    @Operation(summary = "Google linki istifadə edərək asan login üçün endpoint")
    public ResponseEntity<TokenResponse> googleLogin(@RequestBody Map<String, String> body) throws Exception {
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
    @PostMapping("/refresh-token")
    public TokenResponse refresh(@RequestHeader("Authorization") String token) {
        token=token.substring(7);
        String email=jwtService.extractEmail(token);
        String accessToken = jwtService.generateAccessToken(token, null);
        String refreshToken = jwtService.generateRefreshToken(token);
        Optional<User> userOptional = userRepository.findByEmail(email);
        userOptional.get().setRefreshToken(refreshToken);
        redisService.saveTokenToRedis(accessToken, email);
        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }
}
