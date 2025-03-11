package com.sarkhan.backend.config;

import com.sarkhan.backend.model.user.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/public/**","/swagger-ui/**","/v3/api-docs/**","/swagger-ui.html").permitAll() // Herkese açık URL'ler
                        .anyRequest().authenticated() // Geri kalan her şey kimlik doğrulama gerektirir
                )
                .oauth2Login(oauth2 -> oauth2 // OAuth2 kimlik doğrulamasını etkinleştir
                        .defaultSuccessUrl("/home", true) // Giriş başarılı olduğunda yönlendirilecek sayfa

                        .successHandler((request, response, authentication) -> {
                                CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();

                                response.setHeader("Authorization", "Bearer " + customUser.getJwtToken());
                        })
                );

        return http.build();
    }
}
