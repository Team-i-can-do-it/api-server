package com.icando.member.service;


import com.icando.member.entity.ActivityType;
import com.icando.member.entity.Member;
import com.icando.member.entity.PointHistory;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import com.icando.member.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final MemberRepository memberRepository;

    public void earnPoints(Long memberId, int getPoint, ActivityType activityType) {

        LocalDate today = LocalDate.now();
        int todayEarnPoint = pointHistoryRepository.countByMemberIdAndCreatedAt(memberId,today);

        if(todayEarnPoint > 3){
            throw new MemberException(MemberErrorCode.EXCEEDED_EARN_POINT);
        }

        Member member = validateMember(memberId);

        member.addPoints(getPoint);

        PointHistory pointHistory = PointHistory.of(member,getPoint,activityType);

        pointHistoryRepository.save(pointHistory);
    }

    public void usedPoint(Long memberId,int itemPoint, ActivityType activityType){

        Member member = validateMember(memberId);

        member.decreasePoint(itemPoint);

        PointHistory pointHistory = PointHistory.of(member,itemPoint,activityType);

        pointHistoryRepository.save(pointHistory);
    }

    private Member validateMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));
    }


}
