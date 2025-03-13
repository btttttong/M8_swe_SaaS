package com.swe.saas.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendAlertEmail(String recipient, String repo, String eventType, String detailsUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(recipient);
            helper.setSubject("GitHub Alert: " + eventType + " in " + repo);
            helper.setText("An event [" + eventType + "] has been detected in the repository: " + repo +
                           "\nDetails: " + detailsUrl);

            mailSender.send(message);
            System.out.println("Email sent to: " + recipient);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}