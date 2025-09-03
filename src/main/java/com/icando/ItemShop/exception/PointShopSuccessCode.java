package com.icando.ItemShop.exception;

import com.icando.global.success.SuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PointShopSuccessCode implements SuccessCode {

    SUCCESS_DRAW_RANDOM_BOX(HttpStatus.OK,"뽑기에 성공하셨습니다" ),
    SUCCESS_CREATE_ITEM(HttpStatus.OK,"상품이 성공적으로 등록되었습니다" ),
    SUCCESS_GET_POINT_HISTORY(HttpStatus.OK,"포인트 사용 내역을 성공적으로 조회했습니다" );
    SUCCESS_BUY_ITEM(HttpStatus.OK,"상품을 성곡적으로 구매했습니다" ),
    SUCCESS_GET_ITEM_LIST(HttpStatus.OK,"상품 목록을 성공적으로 조회했습니다" ),
    SUCCESS_GET_ITEM(HttpStatus.OK,"상품 정보를 성공적으로 조회했습니다" ),
    SUCCESS_EDIT_ITEM_QUANTITY(HttpStatus.OK,"상품 수량이 성공적으로 수정되었습니다" ),
    SUCCESS_DELETE_ITEM(HttpStatus.OK,"상품을 성공적으로 삭제했습니다" );


    private final HttpStatus status;
    private final String message;

    PointShopSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

