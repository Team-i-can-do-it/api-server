package com.icando.member.service;

import com.icando.member.dto.MyPageResponse;
import com.icando.member.entity.Mbti;
import com.icando.member.entity.Member;
import com.icando.member.entity.PointHistory;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MbtiRepository;
import com.icando.member.repository.MemberRepository;
import com.icando.member.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {


    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;
    private final MbtiRepository mbtiRepository;

    public MyPageResponse searchMyPage(String email) {

        Member member = memberRepository.findByEmail(email).orElseThrow(
                ()-> new MemberException(MemberErrorCode.MEMBER_EMAIL_NOT_FOUND));

        PointHistory point = pointRepository.findPointByMemberId(member.getId()).orElseThrow(
                () -> new MemberException(MemberErrorCode.POINT_IS_NOT_FOUND));

        Mbti mbti = mbtiRepository.findFirstByMemberIdOrderByModifiedAtDesc(member.getId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MBTI_NOT_FOUND));

        return MyPageResponse.of(
                member.getName(),
                point.getPoints(),
                mbti.getId(),
                mbti.getName()
        );
    }
}




