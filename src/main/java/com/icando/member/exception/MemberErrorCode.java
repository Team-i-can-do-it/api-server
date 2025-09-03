package com.icando.member.exception;

import com.icando.global.error.core.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST, "입력하신 회원 ID가 올바르지 않거나 존재하지 않습니다."),
    INVALID_MEMBER_EMAIL(HttpStatus.BAD_REQUEST, "회원의 이메일이 올바르지 않거나 존재하지 않습니다."),
    INVALID_POINT(HttpStatus.BAD_REQUEST, "포인트가 존재하지 않습니다" ),
    NOT_ENOUGH_POINTS(HttpStatus.BAD_REQUEST,"포인트가 부족합니다" ),
    NOT_ADMIN_MEMBER(HttpStatus.BAD_REQUEST,"관리자 계정이 아닙니다" ),
    MEMBER_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "이메일이 없습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return "[MEMBER ERROR]" + message;
    }
}