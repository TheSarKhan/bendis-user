package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.LoginRequest;
import com.sarkhan.backend.dto.RegisterRequest;
import com.sarkhan.backend.dto.TokenResponse;

public interface AuthenticationService {
     TokenResponse register(RegisterRequest request);
    TokenResponse login(LoginRequest request);

}
