package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.authorization.LoginRequest;
import com.sarkhan.backend.dto.authorization.RegisterRequest;
import com.sarkhan.backend.dto.authorization.TokenResponse;

public interface AuthenticationService {
     TokenResponse register(RegisterRequest request);
    TokenResponse login(LoginRequest request);

}
