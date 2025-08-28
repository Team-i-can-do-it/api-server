package com.icando.randomBox.service;

import com.icando.member.entity.Member;
import com.icando.member.entity.Point;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import com.icando.member.repository.PointRepository;
import com.icando.randomBox.dto.CreateItemRequest;
import com.icando.randomBox.entity.RandomBox;
import com.icando.randomBox.exception.RandomBoxException;
import com.icando.randomBox.repository.RandomBoxRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RandomBoxService {

    private final RandomBoxRepository randomBoxRepository;
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    //TODO: 추후 관리자 계정 생성 기능 구현 후 admin 계정인지 아닌지에 대한 인증 로직 추가 예정
    @Transactional
    public RandomBox createItemByAdminId(CreateItemRequest itemRequest, Long memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));

        RandomBox item = RandomBox.of(itemRequest.getItem(), itemRequest.getQuantity(), itemRequest.getProbability());

        randomBoxRepository.save(item);

        return item;
    }

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
