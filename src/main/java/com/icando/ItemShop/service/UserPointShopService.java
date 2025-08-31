package com.icando.ItemShop.service;

import com.icando.ItemShop.repository.ItemRepository;
import com.icando.member.repository.MemberRepository;
import com.icando.member.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPointShopService {

    private final ItemRepository randomBoxRepository;
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    //    public RandomBox drawRandomBoxByMemberPoint(Long memberId) {
//
//        memberRepository.findById(memberId).
//                orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));
//
//        Point point = pointRepository.findByMemberId(memberId)
//                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_POINT));
//
//        //TODO: 추후 갓챠 포인트 결정 후 감소 포인트 조정 예정
//        point.decreasePoint(10);
//    }
}
