package com.icando.feedback.exception;

import com.icando.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FeedbackErrorCode implements ErrorCode {

    FEEDBACK_GENERATION_FAILED(HttpStatus.NOT_FOUND, "피드백 생성을 실패했습니다."),
    WRITING_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 글을 찾을 수 없습니다."),
    INVALID_MEMBER_ID(HttpStatus.NOT_FOUND, "유효하지 않은 회원 ID입니다."),;

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }


}
