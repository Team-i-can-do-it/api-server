package com.icando.ItemShop.service;


import com.icando.ItemShop.dto.ItemGetType;
import com.icando.ItemShop.dto.PointShopHistoryResponse;
import com.icando.ItemShop.dto.ItemResponse;
import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.entity.PointShopHistory;
import com.icando.ItemShop.exception.PointShopErrorCode;
import com.icando.ItemShop.exception.PointShopException;
import com.icando.ItemShop.repository.ItemRepository;
import com.icando.ItemShop.repository.PointShopHistoryRepository;
import com.icando.member.entity.ActivityType;
import com.icando.member.entity.Member;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import com.icando.member.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPointShopService {

    private final ItemRepository itemRepository;

    private final PointShopHistoryRepository pointShopHistoryRepository;
    private final MemberRepository memberRepository;
    private final PointService pointService;

    public List<ItemResponse> getItemList(ItemGetType itemGetType) {

        List<Item> itemList = itemRepository.getItemByPrice(itemGetType);

        return itemList.stream()
                .map(item -> new ItemResponse(item))
                .toList();
    }

    public List<PointShopHistoryResponse> getItemHistoryList(String email) {

        Member member = validateMember(email);

        List<PointShopHistory> histories = pointShopHistoryRepository.findTop10ByMemberIdOrderByCreatedAtDesc(member.getId());

        return histories.stream()
                .map(pointShopHistory -> new PointShopHistoryResponse(pointShopHistory))
                .toList();
    }

    @Transactional
    public Item buyItem(Long itemId,String number, String email) {
        Item item = validateItem(itemId);
        Member member = validatePoint(email, item.getPoint());
        PointShopHistory pointShopHistory = PointShopHistory.byPhoneNumber(member,item,number);
        pointService.usedPoint(member.getId(),item.getPoint(), ActivityType.BUY);
        item.decreaseQuantity(1);
        pointShopHistoryRepository.save(pointShopHistory);
        return item;
    }
      
    public ItemResponse getItem(Long itemId) {

       Item item = validateItem(itemId);

       return ItemResponse.of(item);
    }

    private Member validateMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));
    }

    private Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new PointShopException(PointShopErrorCode.INVALID_ITEM_ID));
    }

    private Member validatePoint(String email, int itemPoint) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_EMAIL));

        if (member.getTotalPoint() < itemPoint) {
            throw new PointShopException(PointShopErrorCode.NOT_ENOUGH_MEMBER_POINT);
        }
        return member;
    }
}
