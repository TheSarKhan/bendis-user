package com.sarkhan.backend.email.service;

import com.sarkhan.backend.email.dto.AppealEmail;
import com.sarkhan.backend.email.dto.ConsultationEmail;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendConsultation(ConsultationEmail consultationEmail) throws MessagingException;
    void sendAppeal(AppealEmail appealEmail) throws MessagingException;
}