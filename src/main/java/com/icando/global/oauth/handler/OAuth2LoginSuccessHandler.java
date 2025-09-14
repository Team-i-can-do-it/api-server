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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    private final String FINAL_REDIRECT_URL = "https://e-eum.site/e-eum";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        log.info("OAUTH2 로그인 성공. 토큰을 담은 HTML 페이지를 클라이언트로 전송합니다.");

        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            // 1. Access Token, Refresh Token 생성
            String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
            String refreshToken = jwtService.createRefreshToken();

            // 2. Refresh Token은 Redis에 저장
            jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
            log.info("Refresh Token을 Redis에 저장했습니다.");

            // 3. 토큰을 저장하고 리다이렉트하는 HTML 응답 생성
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>OAuth2 Login</title></head>");
            out.println("<body>");
            out.println("Processing login..."); // 로딩 메시지
            out.println("<script>");
            out.println("localStorage.setItem('accessToken', '" + accessToken + "');");
            out.println("window.location.replace('" + FINAL_REDIRECT_URL + "');"); // 최종 목적지로 이동
            out.println("</script>");
            out.println("</body>");
            out.println("</html>");
            out.flush();

        } catch (Exception e) {
            log.error("소셜 로그인 성공 후 처리 중 에러 발생", e);
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
