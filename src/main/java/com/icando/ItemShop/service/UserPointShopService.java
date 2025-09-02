package com.icando.ItemShop.service;

import com.icando.ItemShop.dto.PointShopHistoryResponse;
import com.icando.ItemShop.dto.ItemResponse;
import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.entity.PointShopHistory;
import com.icando.ItemShop.repository.ItemRepository;
import com.icando.ItemShop.repository.PointShopHistoryRepository;
import com.icando.member.entity.Member;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
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

    public List<ItemResponse> getItemList(String sortCondition) {

        List<Item> itemList = itemRepository.getItemByPrice(sortCondition);

        return itemList.stream()
                .map(item -> new ItemResponse(item))
                .toList();
    }

    public List<PointShopHistoryResponse> getItemHistoryList(String email) {

        Member member = memberRepository.findByEmail(email)
                        .orElseThrow(()-> new MemberException(MemberErrorCode.INVALID_MEMBER_EMAIL));

        List<PointShopHistory> histories = pointShopHistoryRepository.findTop10ByMemberIdOrderByCreatedAtDesc(member.getId());

        return histories.stream()
                .map(pointShopHistory -> new PointShopHistoryResponse(pointShopHistory))
                .toList();
    }
}
