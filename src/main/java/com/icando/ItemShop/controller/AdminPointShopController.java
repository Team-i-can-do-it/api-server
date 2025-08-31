package com.icando.ItemShop.controller;

import com.icando.global.success.SuccessResponse;
import com.icando.ItemShop.dto.CreateItemRequest;
import com.icando.ItemShop.exception.PointShopSuccessCode;
import com.icando.ItemShop.service.AdminPointShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/randomBox")
@RequiredArgsConstructor
public class ItemController {

    private final AdminPointShopService randomBoxService;

    //TODO: 추후 admin 계정을 통해서 등록할 수 있게 처리 예정
    @PostMapping
    public ResponseEntity<SuccessResponse> createItem(
            @Valid @RequestBody CreateItemRequest itemRequest,
            @RequestParam Long memberId) {
        randomBoxService.createItemByAdminId(itemRequest, memberId);

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_CREATE_ITEM));
    }

    //TODO: 갓챠 확률 기준 정하고 다시 작성 예정
//    @PostMapping("/draw")
//    public ResponseEntity<SuccessResponse> drawRandomBox(
//            @RequestParam Long memberId){
//        randomBoxService.drawRandomBoxByMemberPoint(memberId);
//
//        return ResponseEntity.ok(
//                SuccessResponse.of(RandomBoxSuccessCode.SUCCESS_DRAW_RANDOM_BOX));
//    }
}
