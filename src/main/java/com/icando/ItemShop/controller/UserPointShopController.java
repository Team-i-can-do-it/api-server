package com.icando.ItemShop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pointShop")
@RequiredArgsConstructor
public class UserPointShopController {

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
