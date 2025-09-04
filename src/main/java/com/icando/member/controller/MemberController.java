package com.icando.member.controller;

import com.icando.global.success.SuccessCode;
import com.icando.global.success.SuccessResponse;
import com.icando.member.dto.MyPageResponse;
import com.icando.member.dto.SearchMypageDto;
import com.icando.member.exception.MemberSuccessCode;
import com.icando.member.service.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/mypage")
    public ResponseEntity<SuccessResponse<MyPageResponse>> searchMypae(
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        MyPageResponse myPageResponse = memberService.searchMyPage(userDetails.getUsername());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(MemberSuccessCode.MYPAGE_SUCCESS_FOUND, myPageResponse));
    }
}

