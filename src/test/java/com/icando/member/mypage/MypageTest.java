package com.icando.member.mypage;

import com.icando.member.dto.MbtiResponse;
import com.icando.member.dto.MbtiSummaryDto;
import com.icando.member.dto.MyPageResponse;
import com.icando.member.entity.Mbti;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import com.icando.member.repository.MbtiRepository;
import com.icando.member.repository.MemberRepository;
import com.icando.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MypageTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MbtiRepository mbtiRepository;

    private Member member;
    private Mbti mbti;


    @BeforeEach
    public void setMember() {
        member = Member.createLocalMember(
                "최다빈",
                "9636515@test.com",
                "test123",
                Role.USER
        );
        ReflectionTestUtils.setField(member, "id", 1L);
    }

    @Test
    @DisplayName("마이 페이지 조회 테스트")
    public void myPageTest() {

        mbti = Mbti.of(
                "활발한 치와와",
                "활발하게 말을 잘하는 유형",
                "https://www.icando.com/"
        );

        ReflectionTestUtils.setField(mbti, "id", 1L);
        member.addPoints(100);

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        when(mbtiRepository.findFirstByMemberIdOrderByModifiedAtDesc(member.getId()))
                .thenReturn(Optional.of(mbti));

        MyPageResponse myPageResponse = memberService.searchMyPage(member.getEmail());

        assertThat(myPageResponse.getName()).isEqualTo("최다빈");
        assertThat(myPageResponse.getPoint()).isEqualTo(100);
        assertThat(myPageResponse.getMbtiId()).isEqualTo(mbti.getId());
        assertThat(myPageResponse.getMbtiName()).isEqualTo(mbti.getName());

    }

    @Test
    @DisplayName("포인트, mbti 값이 존재하지 않을때")
    public void valueNotFound() {
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        when(mbtiRepository.findFirstByMemberIdOrderByModifiedAtDesc(member.getId()))
        .thenReturn(Optional.empty());

        MyPageResponse myPageResponse = memberService.searchMyPage(member.getEmail());

        assertThat(myPageResponse.getName()).isEqualTo("최다빈");
        assertThat(myPageResponse.getPoint()).isEqualTo(0);
        assertThat(myPageResponse.getMbtiId()).isNull();
        assertThat(myPageResponse.getMbtiName()).isNull();
    }

    @Test
    @DisplayName("MBTI 컬렉션 조회 - 최신 MBTI 포함")
    public void mbtiSearchTest() {

        Mbti mbti1 = Mbti.of("활발한 치와와", "활발하게 말을 잘하는 유형", "https://www.icando.com/");
        Mbti mbti2 = Mbti.of("조용한 고양이", "조용하고 내성적인 유형", "https://www.icando.com/");

        ReflectionTestUtils.setField(mbti1, "id", 1L);
        ReflectionTestUtils.setField(mbti1, "createdAt", LocalDateTime.now().minusDays(1)); // 오래된 값
        ReflectionTestUtils.setField(mbti2, "id", 2L);
        ReflectionTestUtils.setField(mbti2, "createdAt", LocalDateTime.now()); // 최신값

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));

        when(mbtiRepository.findFirstByMemberIdOrderByModifiedAtDesc(member.getId()))
                .thenReturn(Optional.of(mbti2));

        when(mbtiRepository.findAllByMemberId(member.getId()))
                .thenReturn(List.of(mbti1, mbti2));

        MbtiResponse response = memberService.searchMbti(member.getEmail());

        assertThat(response.getRecentlyMbtiId()).isEqualTo(mbti2.getId());
        assertThat(response.getRecentlyMbtiName()).isEqualTo(mbti2.getName());

        List<Long> mbtiIds = response.getMbtiList().stream()
                .map(MbtiSummaryDto::getMbtiId)
                .toList();

        List<String> mbtiNames = response.getMbtiList().stream()
                .map(MbtiSummaryDto::getMbtiName)
                .toList();

        assertThat(mbtiIds).containsExactly(mbti1.getId(), mbti2.getId());
        assertThat(mbtiNames).containsExactly(mbti1.getName(), mbti2.getName());

    }

}
