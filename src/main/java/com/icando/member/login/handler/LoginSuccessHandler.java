package com.icando.member.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icando.global.auth.service.JwtService;
import com.icando.member.entity.Role;
import com.icando.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Value("${spring.jwt.access.expiration}")
    private String accessExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = extractUsername(authentication); // 인증 정보에서 Username(email) 추출
        String roleName = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        if(roleName.startsWith("ROLE_")) {
            roleName = roleName.substring(5);
        }

        Role role = Role.valueOf(roleName);

        String accessToken = jwtService.createAccessToken(email, role);
        String refreshToken = jwtService.createRefreshToken(email, role);

        jwtService.sendAccessTokenAndRefreshToken(response, accessToken, refreshToken);

        memberRepository.findByEmail(email)
                .ifPresent(user -> {
                    jwtService.updateRefreshToken(email, refreshToken);
                });

        String name = memberRepository.findByEmail(email)
                .map(user -> user.getName())
                .orElse("알수없는 사용자");

        // 응답 헤더 설정
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // JSON 직접 작성
        String jsonResponse = String.format(
                "{ \"message\": \"%s\", \"email\": \"%s\", \"accessTokenExpiration\": \"%s\" }",
                "로그인에 성공하였습니다.",
                email,
                accessExpiration,
                name
        );

        //response를 json형태로 써주는 getWriter()
        response.getWriter().write(jsonResponse);

        log.info("로그인에 성공하였습니다. 이메일 : {}", email);
        log.info("발급된 AccessToken 만료 기간 : {}", accessExpiration);
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}

