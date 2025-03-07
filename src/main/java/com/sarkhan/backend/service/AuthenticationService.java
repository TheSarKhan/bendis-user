package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.UserDto;
import com.sarkhan.backend.dto.UserRequest;
import com.sarkhan.backend.dto.UserResponse;
import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserResponse save(UserDto dto) {
        System.out.println("Save metoduna giris edildi");
        User user = User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .nameSoname(dto.getNameSoname())
                .role(Role.USER) // Default olaraq USER təyin edilir
                .build();
        System.out.println("User entity hazir");
        repository.save(user);
        System.out.println("Databaseye melumat getdi");
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        System.out.println("Save isledi");
        System.out.println("Access Token: " + accessToken); // Debug üçün
        System.out.println("Refresh Token: " + refreshToken); // Debug üçün
        return UserResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserResponse auth(UserRequest dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        User user = repository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        System.out.println("Access Token: " + accessToken); // Debug üçün
        System.out.println("Refresh Token: " + refreshToken); // Debug üçün
        return UserResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
