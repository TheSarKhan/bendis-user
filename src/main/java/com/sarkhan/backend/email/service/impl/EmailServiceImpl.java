package com.sarkhan.backend.email.service.impl;

import com.sarkhan.backend.email.dto.AppealEmail;
import com.sarkhan.backend.email.dto.ConsultationEmail;
import com.sarkhan.backend.email.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;


    @Override
    public void sendConsultation(ConsultationEmail consultationEmail) throws MessagingException {
        String nameSurname = consultationEmail.getNameSurname();
        String email = consultationEmail.getEmail();
        String phone = consultationEmail.getPhone();
        String area = consultationEmail.getArea();

        String adminHtmlContent = "<html><body style='font-family: Arial, sans-serif; background-color: #f8f8f8; padding: 20px;'>"
                + "<div style='background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);'>"
                + "<h2 style='color: #d32f2f; text-align: center;'>📢 Yeni Konsultasiya Tələbi (Təcrübə)</h2>"
                + "<p><strong>👤 Ad Soyad:</strong> " + nameSurname + "</p>"
                + "<p><strong>📧 Email:</strong> " + email + "</p>"
                + "<p><strong>📞 Telefon:</strong> " + phone + "</p>"
                + "<p><strong>📍 Sahə:</strong> " + area + "</p>"
                + "<p style='margin-top: 20px; font-size: 14px; color: #888;'>Bu e-poçt sistem tərəfindən avtomatik göndərilmişdir.</p>"
                + "</div></body></html>";

        String userHtmlContent = "<html><body style='font-family: Arial, sans-serif; background-color: #f8f8f8; padding: 20px;'>"
                + "<div style='background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); text-align: center;'>"
                + "<h2 style='color: #1976D2;'>🎉 Hörmətli " + nameSurname + "!</h2>"
                + "<p>Konsultasiya tələbini qəbul etdik ✅</p>"
                + "<p>Ən qısa zamanda sizinlə əlaqə saxlayacağıq.</p>"
                + "<p style='margin-top: 20px; font-size: 14px; color: #888;'>Bu e-poçt sistem tərəfindən avtomatik göndərilmişdir.</p>"
                + "</div></body></html>";

        try {
            MimeMessage messageToAdmin = mailSender.createMimeMessage();
            MimeMessageHelper adminHelper = new MimeMessageHelper(messageToAdmin, true);
            adminHelper.setTo("benovsebektas@gmail.com");
            adminHelper.setSubject("Yeni Konsultasiya Bildirişi");
            adminHelper.setText(adminHtmlContent, true);
            mailSender.send(messageToAdmin);
        }catch (Exception e) {
            System.out.printf("burda eror var");
        }



        MimeMessage messageToUser = mailSender.createMimeMessage();
        MimeMessageHelper userHelper = new MimeMessageHelper(messageToUser, true);
        userHelper.setTo(email);
        userHelper.setSubject("Konsultasiya tələbini qəbul etdik ✅");
        userHelper.setText(userHtmlContent, true);
        mailSender.send(messageToUser);


    }
    public void sendAppeal(AppealEmail appealEmail) throws MessagingException {
        // Kullanıcı bilgilerini al
        String nameSurname = appealEmail.getNameSurname();
        String email = appealEmail.getEmail();
        String phone = appealEmail.getPhone();
        String additionalQuestion = appealEmail.getAdditionalQuestion();

        // **Admin için HTML içeriği oluştur**
        String adminHtmlContent = "<html><body style='font-family: Arial, sans-serif; background-color: #f8f8f8; padding: 20px;'>"
                + "<div style='background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);'>"
                + "<h2 style='color: #d32f2f; text-align: center;'>📧 Yeni Müraciət</h2>"
                + "<p><strong>👤 Ad Soyad:</strong> " + nameSurname + "</p>"
                + "<p><strong>📧 Email:</strong> " + email + "</p>"
                + "<p><strong>📞 Telefon:</strong> " + phone + "</p>"
                + "<p><strong>❓ Əlavə Sual:</strong> " + additionalQuestion + "</p>"
                + "<p style='margin-top: 20px; font-size: 14px; color: #888;'>Bu e-poçt sistem tərəfindən avtomatik göndərilmişdir.</p>"
                + "</div></body></html>";

        // **Kullanıcı için HTML içeriği oluştur**
        String userHtmlContent = "<html><body style='font-family: Arial, sans-serif; background-color: #f8f8f8; padding: 20px;'>"
                + "<div style='background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); text-align: center;'>"
                + "<h2 style='color: #1976D2;'>📩 Hörmətli " + nameSurname + "!</h2>"
                + "<p>Müraciətinizi qəbul etdik və qısa müddət ərzində sizə geridönüş ediləcək ✅</p>"
                + "<p style='margin-top: 20px; font-size: 14px; color: #888;'>Bu e-poçt sistem tərəfindən avtomatik göndərilmişdir.</p>"
                + "</div></body></html>";

        // **Admin'e e-posta gönder**
        MimeMessage messageToAdmin = mailSender.createMimeMessage();
        MimeMessageHelper adminHelper = new MimeMessageHelper(messageToAdmin, true);
        adminHelper.setTo(from);
        adminHelper.setSubject("Yeni Müraciət Bildirişi");
        adminHelper.setText(adminHtmlContent, true);
        mailSender.send(messageToAdmin);

        // **Kullanıcıya e-posta gönder**
        MimeMessage messageToUser = mailSender.createMimeMessage();
        MimeMessageHelper userHelper = new MimeMessageHelper(messageToUser, true);
        userHelper.setTo(email);
        userHelper.setSubject("Müraciətiniz qəbul edildi ✅");
        userHelper.setText(userHtmlContent, true);
        mailSender.send(messageToUser);
    }


}