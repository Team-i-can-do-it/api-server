package com.icando.member.login.controller;

import com.icando.global.auth.service.JwtService;
import com.icando.member.login.dto.*;
import com.icando.member.login.exception.AuthErrorCode;
import com.icando.member.login.exception.AuthException;
import com.icando.member.login.exception.AuthSuccessCode;
import com.icando.member.login.service.LoginService;
import com.icando.global.success.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

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
