package com.icando.global.mail.exception;

import com.icando.global.error.core.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum MailErrorCode implements ErrorCode {

    MAIL_ERROR_CODE(HttpStatus.BAD_REQUEST, "이메일 오류 발생"),
    CODE_INVALID(HttpStatus.BAD_REQUEST, "인증코드 만료되었습니다."),
    CODE_IS_NOT_CORRECT(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    CODE_OK(HttpStatus.OK, "인증 성공");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return "[MAIL ERROR]" + message;
    }
}
