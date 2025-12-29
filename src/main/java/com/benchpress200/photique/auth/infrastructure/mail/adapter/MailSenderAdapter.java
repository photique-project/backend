package com.benchpress200.photique.auth.infrastructure.mail.adapter;

import static java.lang.Boolean.TRUE;

import com.benchpress200.photique.auth.application.command.port.out.mail.MailSenderPort;
import com.benchpress200.photique.auth.domain.vo.MailContent;
import com.benchpress200.photique.auth.infrastructure.exception.MailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailSenderAdapter implements MailSenderPort {
    private static final String ENCODING = "UTF-8";

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMail(MailContent mailContent) {
        MimeMessage mimeMessage = createMimeMessage(mailContent);
        javaMailSender.send(mimeMessage);
    }

    private MimeMessage createMimeMessage(MailContent mailContent) {
        String email = mailContent.getEmail();
        String subject = mailContent.getSubject();
        String code = mailContent.getCode();

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, TRUE, ENCODING);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(code, TRUE);

            return message;
        } catch (MessagingException e) {
            throw new MailSendException(e.getMessage());
        }
    }
}
