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
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final MemberRepository memberRepository;

    public void earnPoints(Long memberId, int getPoint, ActivityType activityType) {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atStartOfDay().withHour(23).withMinute(59).withSecond(59);
        Member member = validateMember(memberId);


        List<PointHistory> todayEarnPoint = pointHistoryRepository.findByMemberIdAndCreatedAtBetween(memberId,startOfDay,endOfDay);

        long isActivityWordCount = todayEarnPoint.stream().
                filter(PointHistory -> PointHistory.getActivityType() == ActivityType.WORD).count();
        long isActivityTopicCount = todayEarnPoint.stream().
                filter(PointHistory -> PointHistory.getActivityType() == ActivityType.TOPIC).count();

        if (isActivityWordCount + isActivityTopicCount >= 3 ) {
            return;
            //throw new MemberException(MemberErrorCode.EXCEEDED_EARN_POINT);
        }
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
