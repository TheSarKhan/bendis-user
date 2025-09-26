package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.authorization.TokenResponse;
import com.sarkhan.backend.dto.user.UserResponse;
import com.sarkhan.backend.dto.user.UserUpdateRequest;
import com.sarkhan.backend.model.seller.Seller;
import com.sarkhan.backend.service.SellerService;
import com.sarkhan.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Operations related to users and sellers")
public class UserController {
    private final SellerService sellerService;

    private final UserService userService;

    @GetMapping("/current")
    @Operation(summary = "Get current user",
            description = "Returns the currently authenticated user's information")
    public ResponseEntity<UserResponse> getCurrentUser() throws AuthException {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping(name = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update user information",
            description = "Updates user profile with the provided data and profile image")
    public ResponseEntity<TokenResponse> updateUser(@RequestPart @Valid UserUpdateRequest request,
                                                    @RequestPart(required = false) MultipartFile profileImage) throws AuthException, IOException {
        return ResponseEntity.ok(userService.updateUser(request, profileImage));
    }

    @GetMapping("/seller")
    public ResponseEntity<Seller> getSeller(@RequestParam("id") Long id){
        Seller seller = sellerService.getById(id);
        return ResponseEntity.ok(seller);
    }
}
