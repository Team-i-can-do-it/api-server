package com.icando.member.service;

import com.icando.member.dto.MbtiRecentlyDto;
import com.icando.member.dto.MyPageResponse;
import com.icando.member.dto.SearchMypageDto;
import com.icando.member.entity.Member;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MbtiRepository;
import com.icando.member.repository.MemberRepository;
import com.icando.member.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

        SearchMypageDto searchMypageDto = memberRepository.findMyPageByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MYPAGE_NOT_FOUND));

        List<MbtiRecentlyDto> mbtiList = memberRepository.findRecentlyMbti(email, PageRequest.of(0, 1));

        if (mbtiList.isEmpty()) {
            throw new MemberException(MemberErrorCode.MBTI_NOT_FOUND);
        }

        MbtiRecentlyDto recently = mbtiList.get(0);

        return MyPageResponse.of(searchMypageDto, recently);
    }

}




