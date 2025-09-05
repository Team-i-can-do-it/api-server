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
    MEMBER_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "이메일이 없습니다."),
    MYPAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "마이페이지 정보를 찾을 수 없습니다."),
    POINT_IS_NOT_FOUND(HttpStatus.BAD_REQUEST, "포인트를 가져올 수 없습니다."),
    EXCEEDED_EARN_POINT(HttpStatus.BAD_REQUEST,"하루 포인트 적립 횟수는 3회까지 가능합니다." ),
    MBTI_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 MBTI가 존재하지 않습니다."),
    MBTI_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MBTI 저장 실패");

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