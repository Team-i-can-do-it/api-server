package com.icando.ItemShop.exception;

import com.icando.global.error.core.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum RandomBoxErrorCode implements ErrorCode {

    INVALID_POST_ID(HttpStatus.BAD_REQUEST,"해당한 게시글이 존재하지 않습니다." ),
    OUT_OF_STOCK(HttpStatus.CONFLICT,"해당 상품의 수량이 부족합니다");


    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return "[RANDOM BOX ERROR]" + message;
    }
}