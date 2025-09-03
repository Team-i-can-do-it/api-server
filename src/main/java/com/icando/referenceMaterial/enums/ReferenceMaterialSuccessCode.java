package com.icando.referenceMaterial.enums;

import com.icando.global.success.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReferenceMaterialSuccessCode implements SuccessCode {
    GENERATE_SUCCESS(HttpStatus.CREATED, "생성 성공");

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
