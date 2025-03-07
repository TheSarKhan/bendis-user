package com.sarkhan.backend.contoller;

import com.sarkhan.backend.dto.UserDto;
import com.sarkhan.backend.dto.UserRequest;
import com.sarkhan.backend.dto.UserResponse;
import com.sarkhan.backend.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/save")
    public ResponseEntity<UserResponse> save(@RequestBody UserDto dto) {
        System.out.println("Kontroller isledi");
        return ResponseEntity.ok(authenticationService.save(dto));
    }

    @PostMapping("/auth")
    public ResponseEntity<UserResponse> auth(@RequestBody UserRequest dto) {
        System.out.println("Kontroller isledi");
        return ResponseEntity.ok(authenticationService.auth(dto));
    }

}
