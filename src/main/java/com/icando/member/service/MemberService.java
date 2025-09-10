package com.icando.member.service;

import com.icando.member.dto.MbtiResponse;
import com.icando.member.dto.MbtiSummaryDto;
import com.icando.member.dto.MyPageResponse;
import com.icando.member.dto.PointHistoryResponse;
import com.icando.member.entity.Mbti;
import com.icando.member.entity.Member;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.login.exception.AuthErrorCode;
import com.icando.member.login.exception.AuthException;
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

        validateMember(email);;

        Mbti mbti = mbtiRepository.findFirstByMemberIdOrderByModifiedAtDesc(validateMember(email).getId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MBTI_NOT_FOUND));

        return MyPageResponse.of(
                validateMember(email).getName(),
                validateMember(email).getTotalPoint(),
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
          
    //가지고 있는 MBTI를 다 가져온다.
    public MbtiResponse searchMbti(String email) {

        validateMember(email);

        Mbti mbti = mbtiRepository.findFirstByMemberIdOrderByModifiedAtDesc(validateMember(email).getId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MBTI_NOT_FOUND));

        List<MbtiSummaryDto> mbtiList = mbtiRepository.findAllByMemberId(validateMember(email).getId())
                .stream()
                .map(m -> new MbtiSummaryDto(m.getId(), m.getName()))
                .toList();

        return MbtiResponse.of(
                mbti.getId(),
                mbti.getName(),
                mbtiList
        );
    }

    private Member validateMember(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_EMAIL_NOT_FOUND)
        );
        return member;

    }

    public void deleteMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.EMAIL_INVALID));
        mbtiRepository.deleteAllByMemberId(member.getId());
        memberRepository.delete(member);
    }
}




