package com.icando.ItemShop.controller;

import com.icando.ItemShop.dto.PointShopHistoryResponse;
import com.icando.ItemShop.entity.Item;
import com.icando.global.success.SuccessResponse;
import com.icando.ItemShop.dto.ItemRequest;
import com.icando.ItemShop.exception.PointShopSuccessCode;
import com.icando.ItemShop.service.AdminPointShopService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adminPointShop")
@RequiredArgsConstructor
public class AdminPointShopController {

    private final AdminPointShopService adminPointShopService;

    //TODO: 추후 admin 계정을 통해서 등록할 수 있게 처리 예정
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse> createItem(
            @Valid @ModelAttribute ItemRequest itemRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        adminPointShopService.createItemByAdminId(itemRequest, userDetails.getUsername());

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_CREATE_ITEM));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<SuccessResponse> editItemQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity,
            @AuthenticationPrincipal UserDetails userDetails) {

        Item item = adminPointShopService.editItemQuantityByAdminId(userDetails.getUsername(), quantity, itemId);

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_EDIT_ITEM_QUANTITY, item));

    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<SuccessResponse> deleteItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {

        adminPointShopService.deleteItemByAdminId(itemId, userDetails);

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_DELETE_ITEM));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<Page<PointShopHistoryResponse>>> getAllUserPurchases(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable) {

        Page<PointShopHistoryResponse> purchasesPage = adminPointShopService.getAllUserPurchasesByAdminId(userDetails.getUsername(),pageable);

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_GET_ALL_USER_PURCHASES,purchasesPage));
    }
}
