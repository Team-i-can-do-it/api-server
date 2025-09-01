package com.icando.member.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * JSON 기반 로그인 요청을 처리하는 Custom Filter
 * 기본 UsernamePasswordAuthenticationFilter를 상속해서 /login 요청 시
 * email/password를 JSON Body에서 읽어와 인증 처리
 */

@RequiredArgsConstructor
public class CustomJsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String CONTENT_TYPE = "application/json";
    private static final String USERNAME_KEY = "email";
    private static final String PASSWORD_KEY = "password";

    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        if (request.getContentType() == null || !request.getContentType().startsWith(CONTENT_TYPE)) {
            throw new AuthenticationServiceException(
                    "Authentication Content-Type not supported: " + request.getContentType()
            );
        }

        try {
            String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            Map<String, String> loginData = objectMapper.readValue(messageBody, Map.class);

            String email = loginData.get(USERNAME_KEY);
            String password = loginData.get(PASSWORD_KEY);

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(email, password);

            setDetails(request, authRequest);

            return this.getAuthenticationManager().authenticate(authRequest);

        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to parse authentication request body");
        }
    }
}
