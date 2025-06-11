package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.seller.SellerRequestDTO;
import com.sarkhan.backend.dto.seller.SellerResponseDTO;
import com.sarkhan.backend.dto.seller.UpdateSellerRequestDto;
import com.sarkhan.backend.exception.DataNotFoundException;
import com.sarkhan.backend.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}")
    public ResponseEntity<SellerResponseDTO> getById(@PathVariable Long id) throws DataNotFoundException {
        return ResponseEntity.ok(sellerService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SellerResponseDTO> add(@RequestBody SellerRequestDTO sellerRequestDTO,
                                                 @RequestHeader("Authorization") String token) throws DataNotFoundException {
        return ResponseEntity.ok(sellerService.createSeller(sellerRequestDTO, token));
    }

    @PutMapping
    public ResponseEntity<SellerResponseDTO> update(@RequestBody UpdateSellerRequestDto updateSellerRequestDto,
                                                    @RequestHeader("Authorization") String token) throws DataNotFoundException {
        return ResponseEntity.ok(sellerService.update(updateSellerRequestDto, token));
    }
}
