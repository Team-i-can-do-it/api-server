package com.icando.ItemShop.service;

import com.icando.global.upload.S3Uploader;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import com.icando.ItemShop.dto.ItemRequest;
import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPointShopService {

    private final ItemRepository randomBoxRepository;
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;

    //TODO: 추후 관리자 계정 생성 기능 구현 후 admin 계정인지 아닌지에 대한 인증 로직 추가 예정 , user계정에 대한 예외사항 테스트 코드 작성
    @Transactional
    public Item createItemByAdminId(ItemRequest itemRequest, String email) {

        memberRepository.findByEmail(email)
                .orElseThrow(()-> new MemberException(MemberErrorCode.INVALID_MEMBER_EMAIL));

        String imageUrl = s3Uploader.upload(itemRequest.getImageUrl(),"item");

        Item item = Item.of(itemRequest.getName(), imageUrl, itemRequest.getQuantity(),itemRequest.getPoint());

        randomBoxRepository.save(item);

        return item;
    }
}
