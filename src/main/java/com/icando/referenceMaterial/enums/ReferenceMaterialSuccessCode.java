package com.icando.referenceMaterial.enums;

import com.icando.global.success.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReferenceMaterialSuccessCode implements SuccessCode {
    REFERENCE_MATERIAL_READ_SUCCESS(HttpStatus.OK, "참고자료 조회 성공");

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
