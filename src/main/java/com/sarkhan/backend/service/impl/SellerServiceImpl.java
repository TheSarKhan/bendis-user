package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.seller.SellerResponseDTO;
import com.sarkhan.backend.handler.exception.DataNotFoundException;
import com.sarkhan.backend.handler.exception.ResourceNotFoundException;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.mapper.seller.SellerMapper;
import com.sarkhan.backend.model.seller.Seller;
import com.sarkhan.backend.repository.seller.SellerRepository;
import com.sarkhan.backend.service.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerServiceImpl implements SellerService {

    private final JwtService jwtService;
    private final SellerMapper sellerMapper;
    private final SellerRepository sellerRepository;

    public List<SellerResponseDTO> getAll() {
        return sellerMapper
                .sellersToSellersResponseDto(sellerRepository.findAll());
    }

    @Override
    public Seller getById(Long sellerId) {
        return sellerRepository.findById(sellerId).orElseThrow(() ->
                new ResourceNotFoundException("Seller with id: " + sellerId + " not found"));
    }

    @Override
    public SellerResponseDTO getByToken(String token) throws DataNotFoundException {
        String email = jwtService.extractEmail(token);
        Seller seller = sellerRepository.findByBrandEmail(email).orElseThrow(() -> {
            log.error("Seller not found {}", email);
            return new DataNotFoundException("Seller not found");
        });
        return sellerMapper.sellerToSellerResponseDto(seller);
    }
}
