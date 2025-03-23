package com.sarkhan.backend.email.forgot;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OTPService {

    private final Map<String, String> otpStorage = new HashMap<>();

    public String generateOTP(String email) {
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000); // 6 haneli
        otpStorage.put(email, otp);
        return otp;
    }

    public boolean validateOTP(String email, String otp) {
        return otp.equals(otpStorage.get(email));
    }

    public void clearOTP(String email) {
        otpStorage.remove(email);
    }
}
