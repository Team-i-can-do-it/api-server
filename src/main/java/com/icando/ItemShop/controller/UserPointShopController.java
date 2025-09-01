package com.icando.ItemShop.controller;

import com.icando.ItemShop.dto.ItemResponse;
import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.exception.PointShopSuccessCode;
import com.icando.ItemShop.service.UserPointShopService;
import com.icando.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pointShop")
@RequiredArgsConstructor
public class UserPointShopController {

    private final UserPointShopService userPointShopService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<ItemResponse>>> selectItemList (){

        List<ItemResponse> itemList =  userPointShopService.selectItemList();

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_SELECT_ITEM_LIST,itemList));

    }
}
