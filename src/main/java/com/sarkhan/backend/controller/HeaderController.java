package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.header.HeaderResponse;
import com.sarkhan.backend.service.HeaderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/header")
@SecurityRequirement(name = "bearerAuth")
public class HeaderController {
    private final HeaderService headerService;

    @GetMapping
    public ResponseEntity<HeaderResponse> getHeader() {
        return ResponseEntity.ok(headerService.getHeader());
    }
}
