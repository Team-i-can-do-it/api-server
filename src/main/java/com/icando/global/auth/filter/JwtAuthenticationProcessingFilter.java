package com.icando.global.auth.filter;

import com.icando.global.auth.service.JwtService;
import com.icando.member.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    /**
     * JWT 인증 필터
     * "/login" 이외의 URL요청이 왔을 때 처리하는 필터이다.
     * AccessToken을 헤더에 담아서 요청을 보내고 만료되었다면 RefreshToken을 헤더에 요청한다.
     */

    private final JwtService jwtService;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

        //accessToken 존재하는지 여부 먼저 판별
        String accessToken = jwtService.extractAccessToken(request).orElse(null);

        //있으면 유효성 검사
        if (accessToken != null && jwtService.isTokenValid(accessToken)) {
            // 이메일과 권한을 모두 토큰에서 추출
            Optional<String> emailOpt = jwtService.extractEmail(accessToken);
            Optional<Role> roleOpt = jwtService.extractRole(accessToken);

            if (emailOpt.isPresent() && roleOpt.isPresent()) {
                saveAuthentication(emailOpt.get(), roleOpt.get());
            }

            filterChain.doFilter(request, response);
            return;
        }

        //액세스 토큰 만료시 refreshtoken 확인
        String refreshToken = jwtService.extractRefreshToken(request).orElse(null);

        if(refreshToken != null) {
            Optional<String> emailOpt = jwtService.extractEmail(refreshToken);
            Optional<Role> roleOpt = jwtService.extractRole(refreshToken);

            if(emailOpt.isPresent() && jwtService.isRefreshTokenValid(refreshToken, emailOpt.get())) {
                String newAccessToken = jwtService.createAccessToken(emailOpt.get(), roleOpt.get());
                String newRefreshToken = jwtService.createRefreshToken(emailOpt.get(),  roleOpt.get());

                jwtService.updateRefreshToken(emailOpt.get(), newRefreshToken);
                jwtService.sendAccessTokenAndRefreshToken(response, newAccessToken, newRefreshToken);

                log.info("AccessToken 재발급 클라이언트 재요청");
                filterChain.doFilter(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);

    }

    public void saveAuthentication(String email, Role role) {

        UserDetails userDetailsUser = User.builder()
                .username(email)
                .password("")
                .roles(role.name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null, authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}
