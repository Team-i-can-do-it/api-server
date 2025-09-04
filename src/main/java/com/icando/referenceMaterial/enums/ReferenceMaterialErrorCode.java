package com.icando.referenceMaterial.enums;

import com.icando.global.error.core.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReferenceMaterialErrorCode implements ErrorCode {
    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 주제가 존재하지 않습니다.");

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
