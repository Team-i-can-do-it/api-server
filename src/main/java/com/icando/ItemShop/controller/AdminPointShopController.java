package com.icando.ItemShop.controller;

import com.icando.ItemShop.dto.PointShopHistoryResponse;
import com.icando.ItemShop.entity.Item;
import com.icando.global.success.SuccessResponse;
import com.icando.ItemShop.dto.ItemRequest;
import com.icando.ItemShop.exception.PointShopSuccessCode;
import com.icando.ItemShop.service.AdminPointShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
    name = "어드민 샵 관리",
    description = "어드민 권한을 가진 유저가 상품을 관리 할 수 있습니다."
)
public class AdminPointShopController {

    private final AdminPointShopService adminPointShopService;

    //TODO: 추후 admin 계정을 통해서 등록할 수 있게 처리 예정
    @Operation(
        summary = "새 상품 등록",
        description = "관리자가 새 아이템을 등록합니다."
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse> createItem(
            @Valid @ModelAttribute ItemRequest itemRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        adminPointShopService.createItemByAdminId(itemRequest, userDetails.getUsername());

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_CREATE_ITEM));
    }

    @Operation(
        summary = "상품 수정",
        description = "관리자가 아이템의 수량을 수정합니다."
    )
    @PatchMapping("/{itemId}")
    public ResponseEntity<SuccessResponse> editItemQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity,
            @AuthenticationPrincipal UserDetails userDetails) {

        Item item = adminPointShopService.editItemQuantityByAdminId(userDetails.getUsername(), quantity, itemId);

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_EDIT_ITEM_QUANTITY, item));

    }

    @Operation(
        summary = "상품 삭제",
        description = "관리자가 상품을 삭제합니다."
    )
    @DeleteMapping("/{itemId}")
    public ResponseEntity<SuccessResponse> deleteItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {

        adminPointShopService.deleteItemByAdminId(itemId, userDetails);

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_DELETE_ITEM));
    }

    @Operation(
        summary = "구매 내역 조회",
        description = "관리자가 상품 구매 내역을 10개씩 조회 합니다."
    )
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<PointShopHistoryResponse>>> getAllUserPurchases(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable) {

        Page<PointShopHistoryResponse> purchasesPage = adminPointShopService.getAllUserPurchasesByAdminId(userDetails.getUsername(),pageable);

        return ResponseEntity.ok(
                SuccessResponse.of(PointShopSuccessCode.SUCCESS_GET_ALL_USER_PURCHASES,purchasesPage));
    }
}
