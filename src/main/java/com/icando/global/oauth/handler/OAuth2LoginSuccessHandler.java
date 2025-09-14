package com.icando.global.oauth.handler;

import com.icando.global.auth.service.JwtService;
import com.icando.global.oauth.CustomOAuth2User;
import com.icando.member.entity.Role;
import com.icando.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        log.info("OAUTH2 로그인 성공");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            // USER 역할인 경우에만 토큰 생성 및 리다이렉트
            if (oAuth2User.getRole() == Role.USER) {
                // 1. Access Token, Refresh Token 생성
                String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
                String refreshToken = jwtService.createRefreshToken();

                // 2. DB에 Refresh Token 저장
                jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);

                // 3. 토큰을 쿼리 파라미터에 담아 리다이렉트
                String targetUrl = UriComponentsBuilder.fromUriString("https://e-eum.site/oauth/landing")
                        .queryParam("accessToken", accessToken)
                        .build()
                        .encode(StandardCharsets.UTF_8)
                        .toUriString();

                // 4. 리다이렉트 실행 (이제 응답에 직접 쓰는 코드는 모두 제거)
                response.sendRedirect(targetUrl);
            } else {
                // USER 역할이 아닐 경우 다른 처리 (예: 에러 페이지 또는 기본 페이지로 리다이렉트)
                response.sendRedirect("/"); // 예시: 메인 페이지로 리다이렉트
            }
        } catch (Exception e) {
            log.error("소셜 로그인 성공 후 처리 중 에러 발생", e);
            // 에러 발생 시 처리할 수 있는 로직 추가
            response.sendRedirect("/login?error=true");
            throw new ServletException(e);
        }
    }

    private void setTokensAndSendResponse(HttpServletResponse response, String accessToken, String refreshToken, String email) throws IOException {
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessTokenAndRefreshToken(response, accessToken, refreshToken);

        if (refreshToken != null && email != null) {
            jwtService.updateRefreshToken(email, refreshToken);
        }
    }


    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        setTokensAndSendResponse(response, accessToken, refreshToken, oAuth2User.getEmail());

        jwtService.sendAccessTokenAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
    }
}
