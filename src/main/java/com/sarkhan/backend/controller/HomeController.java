package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.home.HomePageResponseDTO;
import com.sarkhan.backend.service.HomePageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
@Tag(name = "Home", description = "Endpoints related to home page information")
public class HomeController {
    private final HomePageService service;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get home page information",
            description = "Fetches data and content needed for the home page."
    )
    public ResponseEntity<HomePageResponseDTO> getHomePage(){
        return ResponseEntity.ok(service.getHomePageInfo());
    }
}
