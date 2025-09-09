package com.icando.login;

import com.icando.global.auth.service.JwtService;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.Date;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogoutTest {

    private static final String REFRESH_PREFIX = "RT:";
    @InjectMocks
    private JwtService jwtService;


    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    @BeforeEach
    public void setMember() {
        Member member = Member.createLocalMember(
              "최다빈",
              "9636515@test.com",
              "test123",
              Role.USER,
              false
        );
    }

    @BeforeEach
    public void setRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }


    @Test
    @DisplayName("로그아웃 성공")
    public void logoutTest() {

        String email = "9636515@test.com";
        String accessToken = "dortptmxhzms1";
        String bearerHeader = "Header " + accessToken;

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + bearerHeader);

        JwtService spyJwtService = spy(jwtService);
        doReturn(new Date(System.currentTimeMillis() + 60000))
                .when(spyJwtService).getExpiration("Header "+accessToken);

        spyJwtService.logout(request, email);

        //REFRESH 삭제 확인
        verify(redisTemplate, times(1)).delete(REFRESH_PREFIX + email);
    }

}
