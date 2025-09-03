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

    @Transactional
    public void saveMbti(MbtiRequest mbtiRequest, String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_EMAIL));

        Mbti mbti = mbtiRepository.findByName(mbtiRequest.name())
            .orElseThrow(() -> new MemberException(MemberErrorCode.MBTI_NOT_FOUND));

        member.updateMbti(mbti);
    }
}
