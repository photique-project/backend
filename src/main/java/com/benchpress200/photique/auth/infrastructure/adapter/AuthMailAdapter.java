package com.benchpress200.photique.auth.infrastructure.adapter;

import static java.lang.Boolean.TRUE;

import com.benchpress200.photique.auth.domain.port.AuthMailPort;
import com.benchpress200.photique.auth.exception.AuthException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthMailAdapter implements AuthMailPort {
    private final JavaMailSender javaMailSender;

    @Override
    public String sendMail(String email) {
        String code = generateRandomCode();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, TRUE, "UTF-8");

            String htmlContent = code;
            helper.setTo(email);
            helper.setSubject("Photique 메일인증");

            helper.setText(htmlContent, TRUE);

        } catch (MessagingException e) {
            throw new AuthException("Authentication Mail Sender Error", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        javaMailSender.send(message);

        return code;
    }

    private String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int randomNumber = random.nextInt(10);
            code.append(randomNumber);
        }

        return code.toString();
    }
}
