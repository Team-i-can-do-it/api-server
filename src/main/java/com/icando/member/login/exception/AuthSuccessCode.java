package com.icando.member.login.exception;

import com.icando.global.success.SuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthSuccessCode implements SuccessCode {

    LOGIN_SUCCESS(HttpStatus.OK, "로그인이 성공적으로 완료되었습니다."),
    MEMBER_SUCCESS_SIGNUP(HttpStatus.OK, "회원가입이 성공적으로 완료되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃이 성공적으로 완료되었습니다.");

    private final HttpStatus status;
    private final String message;

    AuthSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
