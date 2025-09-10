package com.icando.feedback.enums;

import com.icando.global.success.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FeedbackSuccessCode implements SuccessCode {
    FEEDBACK_SUCCESS(HttpStatus.CREATED, "피드백 성공"),
    FEEDBACK_SUCCESS_DAILY_AVERAGE_SCORES_RETRIEVED(HttpStatus.OK, "일별 피드백 평균 점수 조회 성공");

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
