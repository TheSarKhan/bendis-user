package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.seller.SellerResponseDTO;
import com.sarkhan.backend.handler.exception.DataNotFoundException;

import java.util.List;

public interface SellerService {

    List<SellerResponseDTO> getAll();

    SellerResponseDTO getByToken(String token) throws DataNotFoundException;

}
