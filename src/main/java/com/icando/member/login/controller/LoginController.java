package com.icando.member.login.controller;

import com.icando.global.auth.service.JwtService;
import com.icando.member.login.dto.*;
import com.icando.member.login.exception.AuthErrorCode;
import com.icando.member.login.exception.AuthException;
import com.icando.member.login.exception.AuthSuccessCode;
import com.icando.member.login.service.LoginService;
import com.icando.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(
    name = "회원 로그인",
    description = "로그인 관련 컨트롤러 입니다."
)
public class LoginController {

    private final JwtService jwtService;
    private final LoginService loginService;

    // TEST용
    @Operation(
        summary = "회원 로그인",
        description = "로그인을 할 수 있는 컨트롤러입니다."
    )
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody LoginDto loginDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String accessToken = jwtService.createAccessToken(userDetails.getUsername());
        String refreshToken = jwtService.createRefreshToken();
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return ResponseEntity.ok(tokens);
    }


    @Operation(
        summary = "회원 회원가입",
        description = "회원가입을 할 수 있는 컨트롤러입니다."
    )
    @PostMapping("/join")
    public ResponseEntity<SuccessResponse<JoinResponse>> join(@RequestBody @Valid JoinDto joinDto) {

        loginService.join(joinDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(AuthSuccessCode.MEMBER_SUCCESS_SIGNUP));
    }

    @Operation(
        summary = "회원 로그아웃",
        description = "로그아웃을 할 수 있는 컨트롤러입니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logOut(HttpServletRequest request) {
        String email = jwtService.extractAccessToken(request)
                .flatMap(jwtService::extractEmail)
                .orElseThrow(() -> new AuthException(AuthErrorCode.ACCESS_EXCEPTION));

        jwtService.logout(request, email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(AuthSuccessCode.LOGOUT_SUCCESS));
    }
}
