package com.icando.ItemShop.service;

import com.icando.ItemShop.exception.PointShopErrorCode;
import com.icando.ItemShop.exception.PointShopException;
import com.icando.global.upload.S3Uploader;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import com.icando.member.repository.PointRepository;
import com.icando.ItemShop.dto.CreateItemRequest;
import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPointShopService {

    private final ItemRepository randomBoxRepository;
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;
    private final ItemRepository itemRepository;

    //TODO: 추후 관리자 계정 생성 기능 구현 후 admin 계정인지 아닌지에 대한 인증 로직 추가 예정
    @Transactional
    public Item createItemByAdminId(CreateItemRequest itemRequest, Long memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));

        String imageUrl = s3Uploader.upload(itemRequest.getImageUrl(),"item");

        Item item = Item.of(itemRequest.getName(), imageUrl, itemRequest.getQuantity(),itemRequest.getPoint());

        randomBoxRepository.save(item);

        return item;
    }

    //TODO: 반복 예외 사항 validate 메서드로 통합 예정
    public void deleteItemByAdminId(Long itemId, UserDetails userDetails) {
        validateAdmin(userDetails.getUsername());
        Item item = validateItem(itemId);

        itemRepository.delete(item);
    }

    private Member validateAdmin(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_EMAIL));

        if(member.getRole() == (Role.ADMIN)){
            return member;
        }
        throw new MemberException(MemberErrorCode.NOT_ADMIN_MEMBER);
    }

    private Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new PointShopException(PointShopErrorCode.INVALID_ITEM_ID));
    }
}
