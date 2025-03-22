package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;


    private final EmailService emailService;

    public void sendEmailToAllUsers(String subject, String text) throws MessagingException {
        List<User> users = userRepository.findAll();
        String[] emails = users.stream()
                .map(User::getEmail)
                .toArray(String[]::new);

        emailService.sendBulkEmail(emails, subject, text);
    }

    // Yalnız ADMIN rollu istifadəçilərə e-mail göndərmək üçün metod
    public void sendEmailToAdmins(String subject, String text) throws MessagingException {
        List<User> admins = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .toList();
        String[] adminEmails = admins.stream()
                .map(User::getEmail)
                .toArray(String[]::new);

        emailService.sendBulkEmail(adminEmails, subject, text);
    }
}
