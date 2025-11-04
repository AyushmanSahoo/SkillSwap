package com.skillswap.notificationservice.service;

import com.skillswap.notificationservice.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Sends a plain text email.
     */
    public void sendEmail(NotificationRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getToEmail());
            message.setSubject(request.getSubject());
            message.setText(request.getBody());
            // 'from' is automatically set from the 'spring.mail.username' property

            mailSender.send(message);
            log.info("Email sent successfully to {}", request.getToEmail());
        } catch (MailException e) {
            log.error("Failed to send email to {}", request.getToEmail(), e);
            // In a real app, you might re-queue this
        }
    }
}