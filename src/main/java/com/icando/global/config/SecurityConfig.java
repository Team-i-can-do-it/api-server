package com.icando.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icando.global.auth.filter.JwtAuthenticationProcessingFilter;
import com.icando.global.auth.service.JwtService;
import com.icando.member.login.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.icando.member.login.handler.LoginFailureHandler;
import com.icando.member.login.handler.LoginSuccessHandler;
import com.icando.member.login.service.LoginService;
import com.icando.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
// 웹 보안 기능 활성화 어노테이션 & SecurityFilterChain 기반의 설정을 인식하고 동작하게 한다.
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MemberRepository memberRepository;
    private final RedisConfig redisConfig;

    /**
     * BCryptPasswordEncoder : SpringSecurity에서 제공하는 클래스로 비밀번호 암호화 하는데 필요한 Encoder
     * 같은 비밀번호를 암호화 하더라도, 매 번 다른 값이 도출된다.
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RedisTemplate<String, String > redisTemplate) throws Exception {
        //CSRF 비활성화
        http
                .csrf((auth) -> auth.disable());
        //Form Login 비활성화
        http
                .formLogin((auth) -> auth.disable());
        //HTTP Basic 비활성화
        http
                .httpBasic((auth) -> auth.disable());
        http
                .logout((auth) -> auth.disable());
        //URL 별 권한 설정
        http
                .authorizeHttpRequests(auth -> auth

                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // swagger 접근 허용
                                .requestMatchers("/auth/login").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/writing/**").authenticated()
                                .anyRequest().authenticated());

        //세션을 사용하지 않는 정책
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationProcessingFilter(redisTemplate), CustomJsonUsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);



        // 설정된 보안 구성을 적용하여 SecurityFilterChain 객체 생성
        return http.build();
    }

    /**
     * AuthenticationManager 설정 후 등록
     * PasswordEncoder를 사용하는 AuthenticationProvider 지정 (PasswordEncoder는 위에서 등록한 PasswordEncoder 사용)
     * FormLogin(기존 스프링 시큐리티 로그인)과 동일하게 DaoAuthenticationProvider 사용
     * UserDetailsService는 커스텀 LoginService로 등록
     * 또한, FormLogin과 동일하게 AuthenticationManager로는 구현체인 ProviderManager 사용(return ProviderManager)
     *
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        provider.setUserDetailsService(loginService);
        return new ProviderManager(provider);
    }

    /**
     * 로그인 성공 시 호출되는 LoginSuccessJWTProviderHandler 빈 등록
     */
    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtService, memberRepository);
    }

    /**
     * 로그인 실패 시 호출되는 LoginFailureHandler 빈 등록
     */
    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    /**
     * CustomJsonUsernamePasswordAuthenticationFilter 빈 등록
     * 커스텀 필터를 사용하기 위해 만든 커스텀 필터를 Bean으로 등록
     * setAuthenticationManager(authenticationManager())로 위에서 등록한 AuthenticationManager(ProviderManager) 설정
     * 로그인 성공 시 호출할 handler, 실패 시 호출할 handler로 위에서 등록한 handler 설정
     */
    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
                = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return customJsonUsernamePasswordLoginFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter(RedisTemplate<String, String> redisTemplate) {
        JwtAuthenticationProcessingFilter jwtAuthenticationFilter = new JwtAuthenticationProcessingFilter(jwtService, memberRepository, redisTemplate);
        return jwtAuthenticationFilter;
    }


}
