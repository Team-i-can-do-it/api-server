package com.icando.global.mail.exception;

import com.icando.global.success.SuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MailSuccessCode implements SuccessCode {

    MAIL_SUCCESSD_SEND(HttpStatus.OK, "메일이 성공적으로 전송되었습니다."),
    MAIL_VERIFIED_SUCCESS(HttpStatus.OK, "메일 인증이 성공적으로 완료되었습니다.");

    private final HttpStatus status;
    private final String message;

    MailSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
