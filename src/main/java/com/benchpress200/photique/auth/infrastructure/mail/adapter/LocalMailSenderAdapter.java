package com.benchpress200.photique.auth.infrastructure.mail.adapter;

import com.benchpress200.photique.auth.application.command.port.out.mail.MailSenderPort;
import com.benchpress200.photique.auth.domain.vo.MailContent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({"local", "test"})
@Component
public class LocalMailSenderAdapter implements MailSenderPort {
    @Override
    public void sendMail(MailContent mailContent) {
        // no-op
    }
}
