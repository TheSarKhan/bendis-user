package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.seller.SellerResponseDTO;
import com.sarkhan.backend.handler.exception.DataNotFoundException;
import com.sarkhan.backend.model.seller.Seller;

import java.util.List;

public interface SellerService {

    List<SellerResponseDTO> getAll();

    Seller getById(Long sellerId);

    SellerResponseDTO getByToken(String token) throws DataNotFoundException;

}
