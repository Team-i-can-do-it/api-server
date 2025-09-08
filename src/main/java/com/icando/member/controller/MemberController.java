package com.icando.member.controller;


import com.icando.global.success.SuccessResponse;
import com.icando.member.dto.MbtiResponse;
import com.icando.member.dto.MyPageResponse;
import com.icando.member.dto.PointHistoryResponse;
import com.icando.member.exception.MemberSuccessCode;
import com.icando.member.service.MemberService;
import com.icando.global.success.SuccessCode;
import com.icando.global.success.SuccessResponse;
import com.icando.member.dto.MbtiRequest;
import com.icando.member.exception.MemberSuccessCode;
import com.icando.member.service.MbtiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Tag(
    name = "멤버 컨트롤러",
    description = "멤버 관련 컨트롤러 입니다."
)
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

    @GetMapping("/mypage/mbti")
    public ResponseEntity<SuccessResponse> searchMbti(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        MbtiResponse mbtiResponse = memberService.searchMbti(userDetails.getUsername());

        return ResponseEntity.ok(
                SuccessResponse.of(MemberSuccessCode.MBTI_SUCCESS_FOUND, mbtiResponse)
        );
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

    @GetMapping("/mypage/point")
    public ResponseEntity<SuccessResponse> searchMyPagePoint(@AuthenticationPrincipal UserDetails userDetails){

        List<PointHistoryResponse> response = memberService.searchPointHistory(userDetails.getUsername());

        return ResponseEntity.ok(
                SuccessResponse.of(MemberSuccessCode.POINT_SUCCESS_SEARCH, response)
        );
    }
}

