package com.icando.writing.enums;

import com.icando.global.success.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum WritingSuccessCode implements SuccessCode {

    TOPIC_SELECT_SUCCESS(HttpStatus.OK, "주제 조회 성공"),
    WRITING_CREATE_SUCCESS(HttpStatus.CREATED, "글 저장 성공"),
    WRITING_READ_ALL_SUCCESS(HttpStatus.OK, "전체 글 조회 성공"),
    WRITING_READ_SUCCESS(HttpStatus.OK, "글 조회 성공");

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
