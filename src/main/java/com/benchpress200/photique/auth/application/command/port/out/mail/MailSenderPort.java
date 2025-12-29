package com.benchpress200.photique.auth.application.command.port.out.mail;

import com.benchpress200.photique.auth.domain.vo.MailContent;

public interface MailSenderPort {
    void sendMail(MailContent mailContent);
}
