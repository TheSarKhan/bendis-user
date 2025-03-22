package com.sarkhan.backend.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {
    private final String SECRET_KEY = "067cb10737d19d7ca3cc2954c012a994674e6186b54f8b942731dd10b6d230717dbaa69524fc27ed1df64832a7e8af4aa79414802eb01600bec75f28934a4a89460fc132c54a3f20640068983ef7d63b1d46ea3e796c23874a95e4c86cbca3bebf07c97fb7047719abe72767bd78465268c6c48356ece4298e34964d9c7aca158bdd643daa53f7de666f9739c8aa713d323a2641c3c347c1df51ba687661855476475e7facb2d92d52c15e4d4badbf4164d16c41725b4c2b388d1e8b85242f54f3ce3ca37d4230cf76194798cfea1ec452a634e34e481cd5145ee3fae940c5ead453b242cf164fd47427f859c9d164d3da321c9d7f435cdb8aa57ad3e3c584f3"; // Yine properties'den alabilirsin.

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 saat
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    public String extractEmail(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractEmail(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 gün geçerli
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 saat geçerli
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    public String generateAccessToken(String email, Map<String, Object> claims) {
        if (claims == null) {
            claims = new HashMap<>(); // Eğer null ise boş bir Map oluşturuyoruz
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))  // 1 saat geçerli
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}