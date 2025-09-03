package com.icando.global.auth.filter;

import com.icando.global.auth.service.JwtService;
import com.icando.global.auth.util.PasswordUtil;
import com.icando.member.entity.Member;
import com.icando.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    /**
     * JWT 인증 필터
     * "/login" 이외의 URL요청이 왔을 때 처리하는 필터이다.
     * AccessToken을 헤더에 담아서 요청을 보내고 만료되었다면 RefreshToken을 헤더에 요청한다.
     */

    private static final List<String> NO_CHECK_URL = List.of(
            "/api/v1/swagger-ui.html",
            "/api/v1/swagger-ui/**",
            "/api/v1/v3/api-docs/**",
            "/api/v1/auth/login",
            "/api/v1/auth/join",
            "/api/v1/mail/code/request",
            "/api/v1/mail/code/verify",
            "/"
    );


    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    private final RedisTemplate<String ,String> redisTemplate;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

        String path = request.getRequestURI(); // 예: /api/v1/swagger-ui/swagger-initializer.js
        for (String url : NO_CHECK_URL) {
            // /** 패턴이 붙으면 startsWith로 체크
            String checkUrl = url.replace("/**", "");
            if (path.startsWith(checkUrl)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        //accessToken 존재하는지 여부 먼저 판별
        String accessToken = jwtService.extractAccessToken(request).orElse(null);

        //없으면 에러 발생
        if(accessToken == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "액세스 토큰이 존재하지 않습니다.");
            return;
        }
        //있으면 유효성 검사
        if(jwtService.isTokenValid(accessToken)) {
            jwtService.extractEmail(accessToken)
                    .ifPresent(email -> {
                        log.info("JWT Email: {}", email);
                        memberRepository.findByEmail(email)
                                .ifPresentOrElse(
                                        this::saveAuthentication,
                                        () -> log.warn("이메일을 통한 회원이 보이지 않습니다.: {}", email)
                                );
                    });
            filterChain.doFilter(request, response);
            return;

        }

        //액세스 토큰 만료시 refreshtoken 확인
        String refreshToken = jwtService.extractRefreshToken(request).orElse(null);
        if (refreshToken != null && jwtService.isRefreshTokenValid(refreshToken, findEmailByRefreshToken(refreshToken))) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 유효하지 않습니다.");

    }

    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        // 방식: Redis에서 RefreshToken과 매칭되는 이메일 찾기
        String email = findEmailByRefreshToken(refreshToken);

        if (email != null && jwtService.isRefreshTokenValid(refreshToken, email)) {
            String newAccessToken = jwtService.createAccessToken(email);
            String newRefreshToken = jwtService.createRefreshToken();

            // Redis에 새 RefreshToken 저장
            jwtService.updateRefreshToken(email, newRefreshToken);

            //수정된 메서드 사용 (Bearer 포함)
            jwtService.sendAccessTokenAndRefreshToken(response, newAccessToken, newRefreshToken);

            log.info("토큰 재발급 완료 - 이메일: {}", email);
        } else {
            log.warn("유효하지 않은 RefreshToken: {}", refreshToken);
        }
    }

    // 헬퍼 메서드 추가
    private String findEmailByRefreshToken(String refreshToken) {
        try {
            // Redis에서 RT:* 패턴의 모든 키 검색
            Set<String> keys = redisTemplate.keys("RT:*");

            for (String key : keys) {
                String storedToken = redisTemplate.opsForValue().get(key);
                if (refreshToken.equals(storedToken)) {
                    return key.substring(3); // "RT:" 제거하고 이메일 반환
                }
            }
        } catch (Exception e) {
            log.error("RefreshToken으로 이메일 찾기 실패", e);
        }
        return null;
    }

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws IOException, ServletException {

        jwtService.extractAccessToken(request)
                .filter(token -> {
                    if(!jwtService.isTokenValid(token)) {
                        return false;
                    }
                    if(Boolean.TRUE.equals(redisTemplate.hasKey(token))) {
                        log.warn("블랙리스트 처리된 토큰입니다.");
                        return false;
                    }
                    return true;
                })
                .ifPresent(accessToken -> jwtService.extractEmail(accessToken)
                        .ifPresent(email -> memberRepository.findByEmail(email)
                                .ifPresent(this::saveAuthentication)));
        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(Member myMember) {
        String password = myMember.getPassword();
        if(password == null) {
            password = PasswordUtil.generateRandomPassword();
        }

        UserDetails userDetailsUser = User.builder()
                .username(myMember.getEmail())
                .password(password)
                .roles(myMember.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null, authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}
