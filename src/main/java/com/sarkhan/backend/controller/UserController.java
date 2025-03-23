package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.authorization.SellerRequest;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final SellerService sellerService;

    @PostMapping("/create/seller")
    public ResponseEntity<?>createBrand(@RequestBody SellerRequest sellerRequest,
                                        @RequestHeader("Authorization") String token) {
       token=token.substring(7);
        User user=sellerService.createSeller(sellerRequest, token);
        return ResponseEntity.status(201).body(user);
    }
}
