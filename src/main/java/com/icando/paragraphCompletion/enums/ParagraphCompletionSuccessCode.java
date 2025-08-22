package com.icando.paragraphCompletion.enums;

import com.icando.global.success.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ParagraphCompletionSuccessCode implements SuccessCode {
    RANDOM_WORD_SUCCESS(HttpStatus.OK, "랜덤 단어 조회 성공");

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
