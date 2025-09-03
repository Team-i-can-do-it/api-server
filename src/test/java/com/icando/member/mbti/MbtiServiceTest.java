package com.icando.member.mbti;

import com.icando.member.dto.MbtiRequest;
import com.icando.member.entity.Mbti;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MbtiRepository;
import com.icando.member.repository.MemberRepository;
import com.icando.member.service.MbtiService;
import org.aspectj.weaver.patterns.BindingAnnotationFieldTypePattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MbtiServiceTest {

    @Mock
    private MbtiRepository mbtiRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MbtiService mbtiService;

    private Member member;
    private Mbti mbti;
    private MbtiRequest mbtiRequest;

    @BeforeEach
    void setUp() {
        member = Member.createLocalMember(
            "name",
            "test@exampl.com",
            "1234",
            Role.USER,
            false
        );

        mbti = Mbti.of("무슨 푸들", "푸들임", "dog.png");
        mbtiRequest = new MbtiRequest("푸들", "푸들임 ", "intp.png");
    }

    @Test
    @DisplayName("MBTI 캐릭터 저장 성공")
    public void mbtiSave_success() {
        // given
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(mbtiRepository.findByName(mbtiRequest.name())).thenReturn(Optional.of(mbti));

        // when
        mbtiService.saveMbti(mbtiRequest, member.getEmail());

        // then
        assertThat(member.getMbti()).isEqualTo(mbti);
    }

    @Test
    @DisplayName("MBTI 저장 실패 - 존재하지 않는 회원")
    void saveMbti_fail_memberNotFound() {
        // given
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when & then
        MemberException exception = assertThrows(MemberException.class, () -> {
            mbtiService.saveMbti(mbtiRequest, "wrong@example.com");
        });
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.INVALID_MEMBER_EMAIL);
    }

    @Test
    @DisplayName("MBTI 저장 실패 - 존재하지 않는 MBTI")
    void saveMbti_fail_mbtiNotFound() {
        // given
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(mbtiRepository.findByName(anyString())).thenReturn(Optional.empty());

        // when & then
        MemberException exception = assertThrows(MemberException.class, () -> {
            mbtiService.saveMbti(mbtiRequest, member.getEmail());
        });
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.MBTI_NOT_FOUND);
    }


}
