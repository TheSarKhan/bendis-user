package com.sarkhan.backend.controller;

import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @GetMapping
    public String test(@RequestHeader("Authorization") String token) {
        token=token.substring(7);
        Optional<User>user=userRepository.findByEmail(jwtService.extractEmail(token));
        user.get().getNameAndSurname();
        return "test";
    }
}
