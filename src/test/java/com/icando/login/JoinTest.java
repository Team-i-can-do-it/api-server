package com.icando.login;


import com.icando.global.utils.RedisUtil;
import com.icando.member.entity.Member;
import com.icando.member.login.dto.JoinDto;
import com.icando.member.login.exception.AuthErrorCode;
import com.icando.member.login.exception.AuthException;
import com.icando.member.login.service.LoginService;
import com.icando.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JoinTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RedisUtil redisUtil;

    private JoinDto joinDto() {
        return JoinDto.builder()
                .name("최다빈")
                .email("9636515@test.com")
                .password("chlekqlsxptmxm")
                .build();
    }


    @Test
    @DisplayName("회원가입_성공")
    public void joinTest() {

        JoinDto joinDto = joinDto();

        //Redis에서 인증상태 반환
        when(redisUtil.getData("verified:" + joinDto.getEmail())).thenReturn(joinDto.getEmail());

        //Email검증을 통해 이미 회원가입을 한 회원인지 검증
        when(memberRepository.findByEmail(joinDto.getEmail())).thenReturn(Optional.empty());

        loginService.join(joinDto);

        //memberRepository에 한번만 저장되는지 확인
        verify(memberRepository, times(1)).save(any(Member.class));

        verify(redisUtil, times(1)).deleteData("verified:" + joinDto.getEmail());

    }

    @Test
    @DisplayName("이메일 인증 실패")
    public void verifiedFailTest() {
        JoinDto joinDto = joinDto();

        //Redis에서 값 null꺼내옴
        when(redisUtil.getData("verified:" + joinDto.getEmail())).thenReturn(null);

        //AuthException 발생
        AuthException authException = assertThrows(AuthException.class, () -> loginService.join(joinDto));

        //내가 원한 ErrorCode와 맞는지 확인
        assertEquals(AuthErrorCode.MAIL_VERIFIED_FAILED, authException.getErrorCode());

        verify(memberRepository, never()).save(any());
        verify(redisUtil, never()).deleteData(any());

    }

    @Test
    @DisplayName("이메일 중복")
    public void emailExistsTest() {
        JoinDto joinDto = joinDto();

        when(redisUtil.getData("verified:" + joinDto.getEmail())).thenReturn(joinDto.getEmail());

        when(memberRepository.findByEmail(joinDto().getEmail())).thenReturn(Optional.of(mock(Member.class)));

        AuthException authException = assertThrows(AuthException.class, () -> loginService.join(joinDto));

        assertEquals(AuthErrorCode.MEMBER_ALREADY_EXIST, authException.getErrorCode());

        verify(memberRepository, never()).save(any());
        verify(redisUtil, never()).deleteData(any());


    }
}
