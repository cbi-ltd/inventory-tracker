package org.inventory_tracker.util;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import java.io.UnsupportedEncodingException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendReport(
            String recipient,
            String subject,
            String body,
            byte[] pdf,
            String filename){

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body);
            helper.setFrom("easybabasola@gmail.com", "Fuel Flow (CBI Technologies Ltd)");

            helper.addAttachment(
                    filename,
                    new ByteArrayResource(pdf),
                    "application/pdf"
            );

            mailSender.send(message);

        } 
        catch (MessagingException | UnsupportedEncodingException e) {
            throw new IllegalStateException("Failed to send report email", e);
        }
    }
}
