package com.icando.ItemShop.exception;

import com.icando.global.success.SuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PointShopSuccessCode implements SuccessCode {

    SUCCESS_DRAW_RANDOM_BOX(HttpStatus.OK,"뽑기에 성공하셨습니다" ),
    SUCCESS_CREATE_ITEM(HttpStatus.OK,"상품이 성공적으로 등록되었습니다" );

    private final HttpStatus status;
    private final String message;

    PointShopSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

