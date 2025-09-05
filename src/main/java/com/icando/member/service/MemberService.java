package com.icando.member.service;

import com.icando.member.dto.MbtiResponse;
import com.icando.member.dto.MyPageResponse;
import com.icando.member.dto.RecentlyMbtiResponse;
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

import java.util.List;


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

    //가지고 있는 MBTI를 다 가져온다.
    public MbtiResponse searchMbti(String email) {

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_EMAIL_NOT_FOUND));

        RecentlyMbtiResponse response = RecentlyMbti(email);

        List<Object[]> mbtiList = mbtiRepository.findAllByMemberId(member.getId())
                .stream()
                .map(m -> new Object[]{m.getId(),m.getName()})
                .toList();

        return MbtiResponse.of(
                response.getMbtiId(),
                response.getMbtiName(),
                mbtiList
        );
    }

    //최근에 받은 MBTI를 가져온다.
    public RecentlyMbtiResponse RecentlyMbti(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_EMAIL_NOT_FOUND));
        Mbti mbti = mbtiRepository.findFirstByMemberIdOrderByModifiedAtDesc(member.getId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MBTI_NOT_FOUND));

        return RecentlyMbtiResponse.of(
                mbti.getId(),
                mbti.getName()
        );
    }
}




