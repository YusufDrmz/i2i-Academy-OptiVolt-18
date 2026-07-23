package com.i2i.optivolt.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component

public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${mail.notification.from:voltwise-noreply@example.com}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEnergyAlert(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Sent energy alert email to {}", toEmail);
        } catch (Exception e) {
            log.warn("Failed to send energy alert email to {}", toEmail, e);
            throw e;
        }
    }
}
