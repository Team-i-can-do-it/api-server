package com.icando.ItemShop.service;

import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import com.icando.member.repository.PointRepository;
import com.icando.ItemShop.dto.CreateItemRequest;
import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository randomBoxRepository;
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    //TODO: 추후 관리자 계정 생성 기능 구현 후 admin 계정인지 아닌지에 대한 인증 로직 추가 예정
    @Transactional
    public Item createItemByAdminId(CreateItemRequest itemRequest, Long memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));

        Item item = Item.of(itemRequest.getItem(), itemRequest.getQuantity(), itemRequest.getProbability());

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
