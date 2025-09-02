package com.icando.ItemShop.exception;

import com.icando.global.error.core.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum PointShopErrorCode implements ErrorCode {

    INVALID_ITEM_ID(HttpStatus.BAD_REQUEST,"해당 삼품이 존재하지 않습니다." ),
    OUT_OF_STOCK(HttpStatus.CONFLICT,"해당 상품의 수량이 부족합니다"),
    NOT_ENOUGH_MEMBER_POINT(HttpStatus.CONFLICT,"회원의 포인트가 부족합니다");


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