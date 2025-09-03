package com.icando.ItemShop.service;

import com.icando.ItemShop.dto.ItemRequest;
import com.icando.ItemShop.dto.ItemResponse;
import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.exception.PointShopErrorCode;
import com.icando.ItemShop.exception.PointShopException;
import com.icando.ItemShop.repository.ItemRepository;
import com.icando.ItemShop.repository.ItemRepositoryImpl;
import com.icando.member.entity.Member;
import com.icando.member.entity.Point;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import com.icando.member.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPointShopService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    public List<ItemResponse> getItemList(String sortCondition) {

        List<Item> itemList = itemRepository.getItemByPrice(sortCondition);

        return itemList.stream()
                .map(item -> new ItemResponse(item))
                .toList();
    }

    @Transactional
    public Item buyItem(Long itemId,String number, String email) {
        Member member = validateMember(email);
        Item item = validateItem(itemId);
        Point memberPoint = validatePoint(member.getId(), item.getPoint());
        Item phone = Item.byPhoneNumber(number);
        memberPoint.decreasePoint(item.getPoint());
        item.decreaseQuantity(item.getQuantity());
        pointRepository.save(memberPoint);
        itemRepository.save(phone);

        return item;
    }

    private Member validateMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_EMAIL));
    }

    private Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new PointShopException(PointShopErrorCode.INVALID_ITEM_ID));
    }

    private Point validatePoint(Long memberId, int itemPoint) {
        Point point = pointRepository.findPointByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));

        if (point.getPoint() < itemPoint) {
            throw new PointShopException(PointShopErrorCode.NOT_ENOUGH_MEMBER_POINT);
        }
        return point;
    }

    public ItemResponse getItem(Long itemId) {

       Item item = itemRepository.findById(itemId)
               .orElseThrow(() ->new PointShopException(PointShopErrorCode.INVALID_ITEM_ID));

       return ItemResponse.of(item);
    }
}
