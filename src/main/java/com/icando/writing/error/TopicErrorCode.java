package com.icando.writing.error;

import com.icando.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TopicErrorCode implements ErrorCode {

    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "주제 조회에 실패했습니다.");

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
