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
    name = "멤버 API",
    description = "멤버 관련 컨트롤러 입니다."
)
public class MemberController {


    private final MemberService memberService;
    private final MbtiService mbtiService;

    @Operation(
        summary = "마이페이지 조회",
        description = "유저가 마이페이지를 조회합니다."
    )
    @GetMapping("/myPage")
    public ResponseEntity<SuccessResponse<MyPageResponse>> searchMypae(
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        MyPageResponse myPageResponse = memberService.searchMyPage(userDetails.getUsername());

        return ResponseEntity.ok(
                SuccessResponse.of(MemberSuccessCode.MYPAGE_SUCCESS_FOUND, myPageResponse));
    }

    @Operation(
        summary = "마이페이지 Mbti 조회",
        description = "유저가 마이페이지에서 Mbti를 조회합니다."
    )
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

    @Operation(
        summary = "포인트 조회",
        description = "유저는 자기의 포인트 내역을 조회합니다."
    )
    @GetMapping("/mypage/point")
    public ResponseEntity<SuccessResponse> searchMyPagePoint(@AuthenticationPrincipal UserDetails userDetails){

        List<PointHistoryResponse> response = memberService.searchPointHistory(userDetails.getUsername());

        return ResponseEntity.ok(
                SuccessResponse.of(MemberSuccessCode.POINT_SUCCESS_SEARCH, response)
        );
    }

    @Operation(
            summary = "회원탈퇴",
            description = "회원탈퇴를 할 수 있는 컨트롤러입니다."
    )
    @PostMapping("/delete")
    public ResponseEntity<SuccessResponse> deleteMember(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        memberService.deleteMember(userDetails.getUsername());

        return ResponseEntity.ok(
                SuccessResponse.of(MemberSuccessCode.MEMBER_SUCCESS_DELETED));
    }
}

