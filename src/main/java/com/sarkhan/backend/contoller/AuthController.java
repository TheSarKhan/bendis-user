package com.sarkhan.backend.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/oauth2/user")
    public ResponseEntity<Map<String, Object>> getOAuth2User(@AuthenticationPrincipal OAuth2AuthenticationToken token) {
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        OAuth2User user = token.getPrincipal();
        return ResponseEntity.ok(user.getAttributes());
    }
}

