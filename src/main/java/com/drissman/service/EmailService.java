package com.drissman.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    public void sendOtpEmail(String toEmail, String otpCode) {
        log.info("Tentative d'envoi d'un vrai email OTP à {}", toEmail);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail.isEmpty() ? "no-reply@drissman.cm" : senderEmail);
            message.setTo(toEmail);
            message.setSubject("Votre code de vérification Drissman");
            message.setText("Bonjour,\n\nVotre code de vérification est : " + otpCode + "\n\nCe code est valable pendant 15 minutes.\n\nL'équipe Drissman.");
            
            mailSender.send(message);
            log.info("✅ Email envoyé avec succès !");
        } catch (Exception e) {
            log.error("❌ ERREUR LORS DE L'ENVOI DE L'EMAIL: {}", e.getMessage());
            log.error("Avez-vous bien configuré SPRING_MAIL_USERNAME et SPRING_MAIL_PASSWORD ?");
            // On affiche quand même le code dans la console pour que vous ne soyez pas bloqué pendant les tests
            log.info("CODE DE SECOURS (MOCK) : {}", otpCode);
        }
    }
}
