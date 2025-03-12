package com.sarkhan.backend.contoller;

import com.sarkhan.backend.dto.LoginRequest;
import com.sarkhan.backend.dto.RegisterRequest;
import com.sarkhan.backend.dto.TokenResponse;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {


    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

}
