package com.icando.member.mbti;

import com.icando.member.entity.Mbti;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class MbtiServiceTest {

    Member member;

    @BeforeEach
    void setUp() {
        member = Member.createLocalMember(
            "name",
            "test@exampl.com",
            "1234",
            Role.USER,
            false
        );

    }

    @Test
    @DisplayName("MBTI 캐릭터 저장 성공")
    public void mbtiSave_success() {
        // given & when
        Mbti mbti = Mbti.of("똑똑한 치와와", "똑똑한 치와와의 대한 내용", "test.png");
        member.updateMbti(mbti);

        // then
        assertThat(member.getMbti()).isEqualTo(mbti);
    }



}
