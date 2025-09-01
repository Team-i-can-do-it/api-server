package com.icando.member.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String email;
    private String accessToken;
    private String refreshToken;
}