package com.sarkhan.backend.init;

import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbInitializer {

    private final UserService userService;

    @PostConstruct
    public void init() {
        User admin = User.builder()
                .fullName("Admin")
                .email("admin1234@gmail.com")
                .password("Admin123")
                .role(Role.ADMIN)
                .build();

        log.info("User created :" + userService.save(admin));
    }
}
