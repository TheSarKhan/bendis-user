package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.seller.SellerRequestDTO;
import com.sarkhan.backend.dto.seller.SellerResponseDTO;
import com.sarkhan.backend.dto.seller.UpdateSellerRequestDto;
import com.sarkhan.backend.handler.exception.DataNotFoundException;
import com.sarkhan.backend.service.SellerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @GetMapping
    public ResponseEntity<List<SellerResponseDTO>> getAll() {
        return ResponseEntity.ok(sellerService.getAll());
    }

    @GetMapping("/myInfo")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerResponseDTO> getMyInfo(@RequestHeader("Authorization") String token) throws DataNotFoundException {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(sellerService.getByToken(token));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SellerResponseDTO> add(@RequestBody SellerRequestDTO sellerRequestDTO) throws AuthException {
        return ResponseEntity.ok(sellerService.createSeller(sellerRequestDTO));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<SellerResponseDTO> update(@RequestBody UpdateSellerRequestDto updateSellerRequestDto,
                                                    @RequestHeader("Authorization") String token) throws DataNotFoundException {
        return ResponseEntity.ok(sellerService.update(updateSellerRequestDto, token));
    }
}
