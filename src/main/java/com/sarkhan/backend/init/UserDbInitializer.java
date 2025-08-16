package com.sarkhan.backend.init;

import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.user.Seller;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbInitializer {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userService.findUsersByRole(Role.ADMIN).isEmpty()) {
            User admin = User.builder()
                    .fullName("Admin")
                    .email("admin1234@gmail.com")
                    .password(passwordEncoder.encode("Admin123"))
                    .role(Role.ADMIN)
                    .seller(Seller.builder().build())
                    .build();

            admin = userService.save(admin);

            admin.setUserCode("A" + 10000000 + admin.getId());

            log.info("User created :" + userService.save(admin));
        }
    }
}
