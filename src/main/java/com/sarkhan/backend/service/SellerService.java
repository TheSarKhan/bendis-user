package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.seller.SellerRequestDTO;
import com.sarkhan.backend.dto.seller.SellerResponseDTO;
import com.sarkhan.backend.dto.seller.UpdateSellerRequestDto;
import com.sarkhan.backend.exception.DataNotFoundException;
import com.sarkhan.backend.model.user.User;

import java.util.List;

public interface SellerService {
    SellerResponseDTO createSeller(SellerRequestDTO sellerRequestDTO, String token) throws DataNotFoundException;

    List<SellerResponseDTO> getAll();

    SellerResponseDTO getByToken(String token) throws DataNotFoundException;

    SellerResponseDTO update(UpdateSellerRequestDto updateSellerRequestDto, String token) throws DataNotFoundException;
}
