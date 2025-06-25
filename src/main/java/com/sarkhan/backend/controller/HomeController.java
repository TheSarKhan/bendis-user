package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.home.HomePageResponseDTO;
import com.sarkhan.backend.service.HomePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class HomeController {
    private final HomePageService service;

    @GetMapping
    public ResponseEntity<HomePageResponseDTO> getHomePage(@RequestHeader(value = "Authorization", required = false)
                                                               String authHeader){
        return ResponseEntity.ok(service.getHomePageInfo());
    }
}
