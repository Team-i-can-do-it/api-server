package com.icando.paragraphCompletion.enums;

import com.icando.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ParagraphCompletionErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    WORD_NOT_IN_CONTENT(HttpStatus.BAD_REQUEST, "내용에 포함되어 있지 않은 단어가 있습니다."),
    INVALID_WORD_COUNT(HttpStatus.BAD_REQUEST, "단어 목록이 비어있습니다."),
    INVALID_CONTENT(HttpStatus.BAD_REQUEST, "내용이 비어있습니다."),
    PARAGRAPH_COMPLETION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 문단완성 글이 존재하지 않습니다."),
    INVALID_SORT_PARAMETER(HttpStatus.BAD_REQUEST, "유효하지 않은 정렬 파라미터입니다."),;

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
