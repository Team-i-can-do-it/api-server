package com.icando.writing.error;

import com.icando.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum WritingErrorCode implements ErrorCode {

    WRITING_NOT_FOUND(HttpStatus.NOT_FOUND, "글쓰기 조회를 실패했습니다."),
    INVALID_SORT_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 정렬 요청 형식입니다. 기대 : field,ASC|DESC"),;

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
