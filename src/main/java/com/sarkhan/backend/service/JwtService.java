package com.sarkhan.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtService {

    private String SECRET_KEY = "fakhrihasanzadefakhrihasanzade15" +
            "fakhrihasanzadefakhrihasanzade15fakhrihasanzadefakhrihasanzade15";

    public String findEmail(String token) {
        return exportToken(token, Claims::getSubject);
    }

    private <T> T exportToken(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = Jwts.parser()
                .setSigningKey(getKey())
                .build().parseClaimsJws(token).getBody();
        return claimsTFunction.apply(claims);
    }

    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }

    public boolean tokenControl(String jwt, UserDetails userDetails) {
        final String email = findEmail(jwt);
        return (email.equals(userDetails.getUsername()) &&
                !exportToken(jwt, Claims::getExpiration).before(new Date()));
    }

    public String generateToken(UserDetails user) {
        return generateToken(new HashMap<>(), user, 86400000); // 24 saat (access token)
    }

    public String generateRefreshToken(UserDetails user) {
        return generateToken(new HashMap<>(), user, 604800000); // 7 gün (refresh token)
    }

    private String generateToken(HashMap<String, Object> claims, UserDetails user, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername()) // Burada email qayıdır
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
