package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.authorization.SellerRequest;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.user.Seller;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.SellerService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SellerServiceImpl implements SellerService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public SellerServiceImpl(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public User createSeller(SellerRequest sellerRequest, String token) {
        String email=jwtService.extractEmail(token);
        Optional<User>user=userRepository.findByEmail(email);
        if(user.isPresent()) {
            Seller seller=new Seller();
             seller.setBrandEmail(user.get().getEmail());
seller.setBrandName(sellerRequest.getBrandName());
seller.setNameAndSurname(user.get().getNameAndSurname());
seller.setFatherName(sellerRequest.getFatherName());
seller.setFinCode(user.get().getProfile().getFincode());
seller.setBrandVOEN(sellerRequest.getBrandVOEN());
seller.setBrandPhone(user.get().getProfile().getPhoneNumber());
user.get().getRoles().add(Role.SELLER);
user.get().setSeller(seller);
userRepository.save(user.get());
        }
        return user.get();
    }
}
