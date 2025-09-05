package com.icando.member.service;

import com.icando.member.dto.MyPageResponse;
import com.icando.member.dto.PointHistoryResponse;
import com.icando.member.entity.Mbti;
import com.icando.member.entity.Member;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MbtiRepository;
import com.icando.member.repository.MemberRepository;
import com.icando.member.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {


    private final MemberRepository memberRepository;
    private final MbtiRepository mbtiRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public MyPageResponse searchMyPage(String email) {

        Member member = memberRepository.findByEmail(email).orElseThrow(
                ()-> new MemberException(MemberErrorCode.MEMBER_EMAIL_NOT_FOUND));


        Mbti mbti = mbtiRepository.findFirstByMemberIdOrderByModifiedAtDesc(member.getId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MBTI_NOT_FOUND));

        return MyPageResponse.of(
                member.getName(),
                member.getTotalPoint(),
                mbti.getId(),
                mbti.getName()
        );
    }

    public List<PointHistoryResponse> searchPointHistory(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_EMAIL_NOT_FOUND));

        List<PointHistoryResponse> points = pointHistoryRepository.findAllByMemberId(member.getId())
                .stream()
                .map(ph -> PointHistoryResponse.of(
                        ph.getCreatedAt(),
                        ph.getActivityType(),
                        ph.getPoints()
                        ))
                .toList();
        return points;
    }
}




