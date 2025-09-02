package com.icando.ItemShop.controller;

import com.icando.global.success.SuccessResponse;
import com.icando.ItemShop.dto.CreateItemRequest;
import com.icando.ItemShop.exception.PointShopSuccessCode;
import com.icando.ItemShop.service.AdminPointShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pointShop")
@RequiredArgsConstructor
public class AdminPointShopController {

    private final AdminPointShopService adminPointShopService;

    //TODO: 추후 admin 계정을 통해서 등록할 수 있게 처리 예정
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse> createItem(
            @Valid @ModelAttribute CreateItemRequest itemRequest,
            @RequestParam Long memberId) {
        adminPointShopService.createItemByAdminId(itemRequest, memberId);

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_CREATE_ITEM));
    }
}
