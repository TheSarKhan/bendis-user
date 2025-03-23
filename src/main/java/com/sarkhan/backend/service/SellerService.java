package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.authorization.SellerRequest;
import com.sarkhan.backend.model.user.User;

public interface SellerService {
    User createSeller(SellerRequest sellerRequest,String token);
}
