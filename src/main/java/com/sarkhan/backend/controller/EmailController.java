package com.sarkhan.backend.controller;


import com.sarkhan.backend.dto.EmailRequest;
import com.sarkhan.backend.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {

    private final UserService userService;

    @PostMapping("/send-to-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendEmailToAllUsers(@RequestBody EmailRequest request) {
        try {
            userService.sendEmailToAllUsers(request.getSubject(), request.getText());
            return ResponseEntity.ok("E-mails sent successfully to all users!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send e-mails: " + e.getMessage());
        }
    }

    @PostMapping("/send-to-admins")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> sendEmailToAdmins(@RequestBody EmailRequest request) {
        try {
            userService.sendEmailToAdmins(request.getSubject(), request.getText());
            return ResponseEntity.ok("E-mails sent successfully to admins!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send e-mails: " + e.getMessage());
        }
    }
}