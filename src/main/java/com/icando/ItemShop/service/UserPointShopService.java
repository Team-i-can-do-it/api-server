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
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPointShopService {

    private final ItemRepository itemRepository;

    private final PointShopHistoryRepository pointShopHistoryRepository;
    private final MemberRepository memberRepository;
    private final PointService pointService;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String ID_LIST_KEY_PREFIX = "items:ids:";
    private static final String ITEM_STATIC_KEY_PREFIX = "item::";
    private static final String ITEM_QUANTITY_KEY_PREFIX = "item::quantity:";

    public List<ItemResponse> getItemList(ItemGetType itemGetType) {

        String idListKey = ID_LIST_KEY_PREFIX + itemGetType.name().toLowerCase();

        List<String> cachedItemIds = stringRedisTemplate.opsForList().range(idListKey, 0, -1);

        if (!CollectionUtils.isEmpty(cachedItemIds)) {
            return getItemsFromCache(cachedItemIds);
        }

        synchronized (this) {
            cachedItemIds = stringRedisTemplate.opsForList().range(idListKey, 0, -1);
            if (!CollectionUtils.isEmpty(cachedItemIds)) {
                return getItemsFromCache(cachedItemIds);
            }

            List<Item> itemsFromDb = itemRepository.getItemByPrice(itemGetType);
            if (itemsFromDb.isEmpty()) {
                return Collections.emptyList();
            }

            if (itemsFromDb.isEmpty()) {
                return Collections.emptyList();
            }

            List<ItemResponse> itemsResponse = itemsFromDb.stream()
                    .map(ItemResponse::of)
                    .collect(Collectors.toList());

            cacheItems(idListKey, itemsResponse);

            return itemsResponse;
        }
    }

    // 파이프라인을 통해 ID목록으로 캐시에서 상품 정보를 대량으로 가져온다.
    private List<ItemResponse> getItemsFromCache(List<String> itemIds) {

        List<Object> cachedObjects = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String id : itemIds) {
                connection.hashCommands().hGetAll((ITEM_STATIC_KEY_PREFIX + id).getBytes(StandardCharsets.UTF_8));
                connection.stringCommands().get((ITEM_QUANTITY_KEY_PREFIX + id).getBytes(StandardCharsets.UTF_8));
            }
            return null;
        });

        List<ItemResponse> resultList = new ArrayList<>();
        List<Long> missedItemIds = new ArrayList<>();

        for (int i = 0; i < itemIds.size(); i++) {
            Map<String, String> staticData = (Map<String, String>) cachedObjects.get(i * 2);
            String quantity = (String) cachedObjects.get(i * 2 + 1);

            if (CollectionUtils.isEmpty(staticData) || quantity == null) {
                missedItemIds.add(Long.parseLong(itemIds.get(i)));
                continue;
            }

            resultList.add(new ItemResponse(
                    Long.parseLong(itemIds.get(i)),
                    staticData.get("name"),
                    staticData.get("imageUrl"),
                    Integer.parseInt(quantity),
                    Integer.parseInt(staticData.get("point"))
            ));
        }

        if (!missedItemIds.isEmpty()) {
            List<ItemResponse> newlyFetchedItems = itemRepository.findAllById(missedItemIds)
                    .stream()
                    .map(ItemResponse::of)
                    .collect(Collectors.toList());

            if (!newlyFetchedItems.isEmpty()) {
                resultList.addAll(newlyFetchedItems);
                cacheItems(null, newlyFetchedItems);
            }
        }

        Map<Long, ItemResponse> responseMap = resultList.stream()
                .collect(Collectors.toMap(ItemResponse::getId, item -> item));

        return itemIds.stream()
                .map(id -> responseMap.get(Long.parseLong(id)))
                .collect(Collectors.toList());
    }

    //상품 정보를 캐시에 저장하는 메서드
    private void cacheItems(String idListKey, List<ItemResponse> items) {
        List<String> itemIds = items.stream().map(item -> String.valueOf(item.getId())).collect(Collectors.toList());

        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            if (idListKey != null) {
                byte[] keyBytes = idListKey.getBytes(StandardCharsets.UTF_8);
                connection.keyCommands().del(keyBytes);
                connection.listCommands().rPush(keyBytes,
                        itemIds.stream().map(String::getBytes).toArray(byte[][]::new));
                connection.keyCommands().expire(keyBytes, Duration.ofHours(1).toSeconds());
            }

            for (ItemResponse item : items) {
                String staticKey = ITEM_STATIC_KEY_PREFIX + item.getId();
                String quantityKey = ITEM_QUANTITY_KEY_PREFIX + item.getId();

                Map<byte[], byte[]> staticDataMap = Map.of(
                        "name".getBytes(StandardCharsets.UTF_8), item.getName().getBytes(StandardCharsets.UTF_8),
                        "imageUrl".getBytes(StandardCharsets.UTF_8), item.getImageUrl().getBytes(StandardCharsets.UTF_8),
                        "point".getBytes(StandardCharsets.UTF_8), String.valueOf(item.getPoint()).getBytes(StandardCharsets.UTF_8)
                );

                connection.hashCommands().hMSet(staticKey.getBytes(StandardCharsets.UTF_8), staticDataMap);
                connection.keyCommands().expire(staticKey.getBytes(StandardCharsets.UTF_8), Duration.ofHours(1).toSeconds());

                connection.stringCommands().setEx(quantityKey.getBytes(StandardCharsets.UTF_8),
                        Duration.ofSeconds(10).toSeconds(),
                        String.valueOf(item.getQuantity()).getBytes(StandardCharsets.UTF_8));
            }
            return null;
        });
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
