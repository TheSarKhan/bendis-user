package com.sarkhan.backend.controller;

import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @PostMapping("/set-role")
    public ResponseEntity<String> setRole(@RequestParam String email, @RequestParam Role role) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();
Set<Role> roles=user.getRoles();
roles.add(role);  // Yeni rol ekleniyor
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok("Role updated successfully");
    }
}
