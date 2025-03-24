package com.sarkhan.backend.service.impl;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.sarkhan.backend.dto.authorization.TokenResponse;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String imageUrl = oAuth2User.getAttribute("picture");
        String googleId = oAuth2User.getAttribute("sub");

        TokenResponse authResponse = registerOrLoginUser(email, firstName, lastName, imageUrl, googleId);


        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("accessToken", authResponse.getAccessToken());
        attributes.put("refreshToken", authResponse.getRefreshToken());

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "email");
    }

    private TokenResponse registerOrLoginUser(String email, String firstName, String lastName, String googleId, String imageUrl) {

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {

            User userEntity = existingUser.get();
            userEntity.setGoogleId(googleId);
            String accessToken = jwtService.generateAccessToken(userEntity.getEmail(), null);
            String refreshToken = jwtService.generateRefreshToken(userEntity.getEmail());
            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setNameAndSurname(firstName + " " + lastName);
            newUser.setGoogleId(googleId);
            newUser.setProfileImg(imageUrl);
            newUser.setRoles(Collections.singleton(Role.USER));
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());
            newUser.setPassword(passwordEncoder.encode("default_password"));


            userRepository.save(newUser);


            String accessToken = jwtService.generateAccessToken(newUser.getEmail(), null);
            String refreshToken = jwtService.generateRefreshToken(newUser.getEmail());


            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
    }

    public TokenResponse processGoogleLogin(String idTokenString) throws GeneralSecurityException, IOException {
        String clientId = "407408718192.apps.googleusercontent.com";
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();

        System.out.println("Received idTokenString: " + idTokenString);
        GoogleIdToken idToken = verifier.verify(idTokenString);
        System.out.println("Verified idToken: " + idToken);

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            String userId = payload.getSubject();
            String email = payload.getEmail();
            String pictureUrl = (String) payload.get("picture");
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");

            if (firstName == null) {
                firstName = "Unknown";
            }

            if (lastName == null) {
                lastName = "Unknown";
            }

            return registerOrLoginUser(email, firstName, lastName, userId, pictureUrl);
        } else {
            throw new RuntimeException("Invalid ID token.");
        }
    }
}