package com.icando.ItemShop.controller;

import com.icando.ItemShop.dto.ItemGetType;
import com.icando.ItemShop.dto.PointShopHistoryResponse;
import com.icando.ItemShop.dto.ItemResponse;
import com.icando.ItemShop.exception.PointShopSuccessCode;
import com.icando.ItemShop.service.UserPointShopService;
import com.icando.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pointShop")
@RequiredArgsConstructor
@Tag(
    name = "유저 포인트/샵 API",
    description = "유저 상점 포인트 관련 컨트롤러 입니다."
)
public class UserPointShopController {

    private final UserPointShopService userPointShopService;

    @Operation(
        summary = "상품 리스트 조회",
        description = "유저가 상품 리스트를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<SuccessResponse<List<ItemResponse>>> getItemList (
            @RequestParam ItemGetType itemGetType){

        List<ItemResponse> itemList = userPointShopService.getItemList(itemGetType);

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_GET_ITEM_LIST,itemList));
    }

    @Operation(
        summary = "상품 조회",
        description = "유저가 상품을 조회합니다."
    )
    @GetMapping("/{itemId}")
    public ResponseEntity<SuccessResponse> getItem(
            @PathVariable Long itemId){
        ItemResponse item = userPointShopService.getItem(itemId);

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_GET_ITEM,item));
    }

    @Operation(
        summary = "상품 내역 조회",
        description = "유저가 상품 구매내역을 조회합니다."
    )
    @GetMapping("/pointHistory")
    public ResponseEntity<SuccessResponse<List<PointShopHistoryResponse>>> getPointHistory(
            @AuthenticationPrincipal UserDetails userDetails){

        List<PointShopHistoryResponse> historyList = userPointShopService.getItemHistoryList(userDetails.getUsername());

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_GET_POINT_HISTORY,historyList));
    }
  
    @PostMapping("/{itemId}/buy")
    public ResponseEntity<SuccessResponse> buyItem (
            @PathVariable Long itemId,
            @RequestParam String number,
            @AuthenticationPrincipal UserDetails userDetails) {

        userPointShopService.buyItem(itemId,number,userDetails.getUsername());

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_BUY_ITEM));
    }
}
