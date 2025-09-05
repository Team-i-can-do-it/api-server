package com.icando.member.exception;

import com.icando.global.success.SuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberSuccessCode implements SuccessCode {

    MEMBER_SUCCESS_SIGNUP(HttpStatus.CREATED, "회원가입이 성공적으로 완료되었습니다."),
    MYPAGE_SUCCESS_FOUND(HttpStatus.OK, "마이페이지를 성공적으로 조회하였습니다."),
    MBTI_SUCCESS_SAVE(HttpStatus.CREATED, "MBTI 저장이 성공했습니다.");

    private final HttpStatus status;
    private final String message;

    MemberSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
