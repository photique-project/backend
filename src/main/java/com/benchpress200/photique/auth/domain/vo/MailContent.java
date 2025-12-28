package com.benchpress200.photique.auth.domain.vo;

import lombok.Getter;

@Getter
public class MailContent {
    private static final String MAIL_SUBJECT = "Photique 메일인증";
    private static final String MAIL_BODY = "";

    private String code;
    private String email;
    private String subject;
    private String body;

    private MailContent(
            String code,
            String email,
            String subject,
            String body
    ) {
        this.code = code;
        this.email = email;
        this.subject = subject;
        this.body = body;
    }

    public static MailContent of(
            String code,
            String email,
            String subject,
            String body
    ) {
        return new MailContent(
                code,
                email,
                subject,
                body
        );
    }

    public static MailContent of(String code, String email, String subject) {
        return new MailContent(
                code,
                email,
                subject,
                MAIL_BODY
        );
    }

    public static MailContent of(String code, String email) {
        return new MailContent(
                code,
                email,
                MAIL_SUBJECT,
                MAIL_BODY
        );
    }
}
