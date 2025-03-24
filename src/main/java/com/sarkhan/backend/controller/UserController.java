package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.authorization.SellerRequest;
import com.sarkhan.backend.dto.authorization.TokenResponse;
import com.sarkhan.backend.dto.authorization.UserProfileRequest;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.redis.RedisService;
import com.sarkhan.backend.service.SellerService;
import com.sarkhan.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final SellerService sellerService;
    private final UserService userService;
    private final JwtService jwtService;
    private final RedisService redisService;

    @PostMapping("/create/seller")
    public ResponseEntity<?> createBrand(@RequestBody SellerRequest sellerRequest,
                                         @RequestHeader("Authorization") String token) {
        token = token.substring(7);
        User user = sellerService.createSeller(sellerRequest, token);
        return ResponseEntity.status(201).body(user);
    }

    @PostMapping("/change/user/profile")
    public ResponseEntity<?> completeUserProfile(@RequestHeader("Authorization") String token, @RequestBody UserProfileRequest userProfileRequest) {
        token = token.substring(7);
      User user=userService.updateUserProfile(userProfileRequest, token);
      String accessToken=jwtService.generateAccessToken(user.getEmail(),null);
      String refreshToken=jwtService.generateRefreshToken(user.getEmail());
      redisService.deleteRefreshToken(user.getEmail());
      redisService.deleteTokenFromRedis(user.getEmail());
      redisService.saveTokenToRedis(accessToken,user.getEmail());
      redisService.saveRefreshToken(user.getEmail(),refreshToken,7);
    return ResponseEntity.status(201).body(TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build());
    }
}
