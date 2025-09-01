package com.icando.member.login.dto;

import com.icando.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JoinResponse {

    private Long memberId;
    private String email;

    /**
     * 클라이언트로 전송할 DTO객체 생성
     */
    @Builder
    public JoinResponse(Long memberId, String email) {
        this.memberId = memberId;
        this.email = email;
    }

    public static JoinResponse toResponse(Member member) {
        return JoinResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .build();
    }
}
