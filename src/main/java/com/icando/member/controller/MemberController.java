package com.icando.member.controller;


import com.icando.global.success.SuccessResponse;
import com.icando.member.dto.MyPageResponse;
import com.icando.member.exception.MemberSuccessCode;
import com.icando.member.service.MemberService;
import com.icando.global.success.SuccessCode;
import com.icando.global.success.SuccessResponse;
import com.icando.member.dto.MbtiRequest;
import com.icando.member.exception.MemberSuccessCode;
import com.icando.member.service.MbtiService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {


    private final MemberService memberService;
    private final MbtiService mbtiService;

    @GetMapping("/myPage")
    public ResponseEntity<SuccessResponse<MyPageResponse>> searchMypae(
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        MyPageResponse myPageResponse = memberService.searchMyPage(userDetails.getUsername());

        return ResponseEntity.ok(
                SuccessResponse.of(MemberSuccessCode.MYPAGE_SUCCESS_FOUND, myPageResponse));
    }
  
    @Operation(
        summary = "Mbti 저장",
        description = "Mbti를 저장합니다."
    )
    @PostMapping("/mbti")
    public ResponseEntity<SuccessResponse> saveMbti(
        @Valid MbtiRequest mbtiRequest,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        mbtiService.saveMbti(mbtiRequest, userDetails.getUsername());

        return ResponseEntity.ok(
            SuccessResponse.of(MemberSuccessCode.MBTI_SUCCESS_SAVE)
        );


    }
}

