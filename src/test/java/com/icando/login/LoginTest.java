package com.icando.login;

import com.icando.global.utils.RedisUtil;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import com.icando.member.login.exception.AuthErrorCode;
import com.icando.member.login.exception.AuthException;
import com.icando.member.login.service.LoginService;
import com.icando.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private MemberRepository memberRepository;

    private Member member;


    @BeforeEach
    public void setMember() {
        member = Member.createLocalMember(
                "최다빈",
                "9636515@test.com",
                "test123",
                Role.USER
        );
    }
    @Test
    @DisplayName("로그인 성공")
    public void loginTest() {
        //given
        String email = "9636515@test.com";

        //email로 member확인 후 맞으면 반환
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        //loadUserByUsername(email)을 통해 member값 가져오기
        UserDetails userDetails = loginService.loadUserByUsername(email);

        //then
        assertEquals(member.getEmail(), userDetails.getUsername());
        assertEquals(member.getPassword(), userDetails.getPassword());

        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("없는 회원으로 인한 로그인 실패")
    public void loginFailTest() {
        String email = "9636515@test.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());
        AuthException authException = assertThrows(AuthException.class, () -> loginService.loadUserByUsername(email));

        assertEquals(AuthErrorCode.MEMBER_NOT_FOUND, authException.getErrorCode());
        verify(memberRepository, times(1)).findByEmail(email);
    }
}
