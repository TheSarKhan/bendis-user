package com.sarkhan.backend.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
    @GetMapping
    public ResponseEntity<String> getProduct() {
        return ResponseEntity.ok("200");
    }
}
