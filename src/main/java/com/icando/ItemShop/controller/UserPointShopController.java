package com.icando.ItemShop.controller;

import com.icando.ItemShop.dto.PointShopHistoryResponse;
import com.icando.ItemShop.dto.ItemResponse;
import com.icando.ItemShop.exception.PointShopSuccessCode;
import com.icando.ItemShop.service.UserPointShopService;
import com.icando.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pointShop")
@RequiredArgsConstructor
public class UserPointShopController {

    private final UserPointShopService userPointShopService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<ItemResponse>>> getItemList (
            @RequestParam String sortCondition){

        List<ItemResponse> itemList = userPointShopService.getItemList(sortCondition);

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_SELECT_ITEM_LIST,itemList));
    }

    @GetMapping("/PointHistory")
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
