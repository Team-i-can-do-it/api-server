package com.icando.ItemShop.service;

import com.icando.ItemShop.dto.ItemResponse;
import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.repository.ItemRepository;
import com.icando.member.repository.MemberRepository;
import com.icando.member.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPointShopService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    public List<ItemResponse> selectItemList() {

        List<Item> itemList = itemRepository.findAll();

        return itemList.stream()
                .map(item -> new ItemResponse(item))
                .toList();
    }
}
