package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.seller.SellerRequestDTO;
import com.sarkhan.backend.dto.seller.SellerResponseDTO;
import com.sarkhan.backend.dto.seller.UpdateSellerRequestDto;
import com.sarkhan.backend.exception.DataNotFoundException;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.mapper.seller.SellerMapper;
import com.sarkhan.backend.model.user.Seller;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.SellerRepository;
import com.sarkhan.backend.repository.user.UserRepository;
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
    private final UserRepository userRepository;
    private final SellerMapper sellerMapper;
    private final SellerRepository sellerRepository;

    public List<SellerResponseDTO> getAll() {
        return sellerMapper.sellersToSellersResponseDto(sellerRepository.findAll());
    }

    @Override
    public SellerResponseDTO getByToken(String token) throws DataNotFoundException {
        String email = jwtService.extractEmail(token);
        Seller seller = sellerRepository.findByBrandEmail(email).orElseThrow(() -> {
            log.error("Seller not found {}",email);
            return new DataNotFoundException("Seller not found");
        });
        return sellerMapper.sellerToSellerResponseDto(seller);
    }

    @Override
    public SellerResponseDTO createSeller(SellerRequestDTO sellerRequestDTO, String token) throws DataNotFoundException {
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User not found {}",token);
            return new DataNotFoundException("User not found");
        });
        Seller seller = sellerMapper.sellerRequestDtoToSeller(sellerRequestDTO);
        seller.setBrandEmail(user.getEmail());
        seller.setBrandName(sellerRequestDTO.brandName());
        seller.setFullName(user.getFullName());
        seller.setFatherName(sellerRequestDTO.fatherName());
        seller.setFinCode(sellerRequestDTO.finCode());
        seller.setBrandVOEN(sellerRequestDTO.brandVOEN());
        seller.setBrandPhone(user.getPhoneNumber());
        user.setSeller(seller);
        userRepository.save(user);
        return sellerMapper.sellerToSellerResponseDto(seller);
    }

    @Override
    public SellerResponseDTO update(UpdateSellerRequestDto updateSellerRequestDto, String token) throws DataNotFoundException {
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User not found");
            return new DataNotFoundException("User not found");
        });
        Seller seller = user.getSeller();
        seller.setFullName(updateSellerRequestDto.fullName());
        seller.setBrandEmail(updateSellerRequestDto.brandEmail());
        seller.setBrandName(updateSellerRequestDto.brandName());
        seller.setFatherName(updateSellerRequestDto.fatherName());
        seller.setBrandVOEN(updateSellerRequestDto.brandVOEN());
        seller.setBrandPhone(updateSellerRequestDto.brandPhone());
        seller.setFinCode(updateSellerRequestDto.finCode());
        user.setSeller(seller);
        userRepository.save(user);
        return sellerMapper.sellerToSellerResponseDto(seller);
    }
}
