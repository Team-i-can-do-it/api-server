package com.icando.ItemShop.service;

import com.icando.ItemShop.dto.PointShopHistoryResponse;
import com.icando.ItemShop.entity.PointShopHistory;
import com.icando.ItemShop.exception.PointShopErrorCode;
import com.icando.ItemShop.exception.PointShopException;
import com.icando.ItemShop.repository.PointShopHistoryRepository;
import com.icando.global.upload.S3Uploader;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import com.icando.ItemShop.dto.ItemRequest;
import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPointShopService {

    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;
    private final ItemRepository itemRepository;
    private final PointShopHistoryRepository pointShopHistoryRepository;

    //TODO: 추후 관리자 계정 생성 기능 구현 후 admin 계정인지 아닌지에 대한 인증 로직 추가 예정 , user계정에 대한 예외사항 테스트 코드 작성
    @Transactional
    public Item createItemByAdminId(ItemRequest itemRequest, String email) {

        validateAdmin(email);

        String imageUrl = s3Uploader.upload(itemRequest.getImageUrl(), "item");

        Item item = Item.of(itemRequest.getName(), imageUrl, itemRequest.getQuantity(), itemRequest.getPoint());

        itemRepository.save(item);

        return item;
    }

    @Transactional
    public Item editItemQuantityByAdminId(String email, int quantity, Long itemId) {

        validateAdmin(email);
        Item item = itemRepository.findById(itemId).
                orElseThrow(() -> new PointShopException(PointShopErrorCode.INVALID_ITEM_ID));

        item.editItemQuantity(quantity);

        itemRepository.save(item);

        return item;
    }

    public void deleteItemByAdminId(Long itemId, UserDetails userDetails) {
        validateAdmin(userDetails.getUsername());
        Item item = validateItem(itemId);

        itemRepository.delete(item);
    }

    public Page<PointShopHistoryResponse> getAllUserPurchasesByAdminId(String email, Pageable pageable) {

        validateAdmin(email);

        Page<PointShopHistory> pointShopHistoriesPage = pointShopHistoryRepository.findAllOrderByCreatedAtDecs(pageable);
        return pointShopHistoriesPage
                .map(pointShopHistory -> new PointShopHistoryResponse(pointShopHistory));
    }

        private Member validateAdmin (String email){
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_EMAIL));

            if (member.getRole() == (Role.ADMIN)) {
                return member;
            }
            throw new MemberException(MemberErrorCode.NOT_ADMIN_MEMBER);
        }

        private Item validateItem (Long itemId){
            return itemRepository.findById(itemId)
                    .orElseThrow(() -> new PointShopException(PointShopErrorCode.INVALID_ITEM_ID));
        }


}
