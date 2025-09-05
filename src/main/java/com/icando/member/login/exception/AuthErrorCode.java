package com.icando.member.login.exception;

import com.icando.global.error.core.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST, "입력하신 회원 ID가 올바르지 않거나 존재하지 않습니다."),
    MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 회원가입이 되어있습니다."),
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),
    ACCESS_EXCEPTION(HttpStatus.BAD_REQUEST, "액세스 토큰이 없습니다."),
    ALREADY_MEMBER_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return "[AUTH ERROR]" + message;
    }
}
