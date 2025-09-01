package com.icando.member.login.controller;

import com.icando.global.auth.service.JwtService;
import com.icando.member.login.dto.*;
import com.icando.member.login.exception.AuthErrorCode;
import com.icando.member.login.exception.AuthException;
import com.icando.member.login.exception.AuthSuccessCode;
import com.icando.member.login.service.LoginService;
import com.icando.global.success.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoginService loginService;


    @PostMapping("/join")
    public ResponseEntity<SuccessResponse<JoinResponse>> join(@RequestBody @Valid JoinDto joinDto) {

        loginService.join(joinDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(AuthSuccessCode.MEMBER_SUCCESS_SIGNUP));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginRequestDto, HttpServletResponse httpResponse) {
        try {
            // 사용자 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );

            // JWT 생성
            String accessToken = jwtService.createAccessToken(loginRequestDto.getEmail());
            String refreshToken = jwtService.createRefreshToken();

            jwtService.updateRefreshToken(loginRequestDto.getEmail(), refreshToken);
            jwtService.sendAccessTokenAndRefreshToken(httpResponse, accessToken, refreshToken);

            // 응답 DTO
            LoginResponse response = new LoginResponse(
                    "로그인에 성공하였습니다.",
                    loginRequestDto.getEmail(),
                    accessToken,
                    refreshToken
            );

            // REST API 응답
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(SuccessResponse.of(AuthSuccessCode.LOGIN_SUCCESS, response));

        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

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
