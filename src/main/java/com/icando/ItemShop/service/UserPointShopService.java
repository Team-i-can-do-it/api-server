package com.icando.ItemShop.service;

import com.icando.ItemShop.dto.ItemResponse;
import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.repository.ItemRepository;
import com.icando.ItemShop.repository.ItemRepositoryImpl;
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

    public List<ItemResponse> getItemList(String sortCondition) {

        List<Item> itemList = itemRepository.getItemByPrice(sortCondition);

        return itemList.stream()
                .map(item -> new ItemResponse(item))
                .toList();
    }
}
