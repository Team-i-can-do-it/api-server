package com.icando.member.controller;

import com.icando.global.success.SuccessCode;
import com.icando.global.success.SuccessResponse;
import com.icando.member.dto.MbtiRequest;
import com.icando.member.exception.MemberSuccessCode;
import com.icando.member.service.MbtiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    MbtiService mbtiService;

    @PostMapping("/mbti")
    public ResponseEntity<SuccessResponse> saveMbti(@Valid MbtiRequest mbtiRequest) {
        mbtiService.saveMbti(mbtiRequest);

        return ResponseEntity.ok(
            SuccessResponse.of(MemberSuccessCode.MBTI_SUCCESS_SAVE)
        );

    }
}

