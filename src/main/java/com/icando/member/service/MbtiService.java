package com.icando.member.service;

import com.icando.member.dto.MbtiRequest;
import com.icando.member.entity.Mbti;
import com.icando.member.entity.Member;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MbtiRepository;
import com.icando.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MbtiService {

    private final MbtiRepository mbtiRepository;
    private final MemberRepository memberRepository;

    // JwtAuthenticationProcessingFilter 참고하면 username을 email로 받아옴
    @Transactional
    public void saveMbti(MbtiRequest mbtiRequest, String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_EMAIL));

        // MBTI가 이미 존재하는지 조회
        if (mbtiRepository.existsByMemberAndName(member, mbtiRequest.name())) {
            throw new MemberException(MemberErrorCode.DUPLICATE_MBTI);
        }

        Mbti newMbti = Mbti.of(
            mbtiRequest.name(),
            mbtiRequest.description(),
            mbtiRequest.imageUrl()
        );
        newMbti.updateMember(member);

        mbtiRepository.save(newMbti);
    }
}
