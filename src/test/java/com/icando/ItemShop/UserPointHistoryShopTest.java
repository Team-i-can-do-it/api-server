package com.icando.ItemShop;

import com.icando.ItemShop.dto.ItemRequest;
import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.exception.PointShopErrorCode;
import com.icando.ItemShop.exception.PointShopException;
import com.icando.ItemShop.repository.ItemRepository;
import com.icando.ItemShop.repository.PointShopHistoryRepository;
import com.icando.ItemShop.service.AdminPointShopService;
import com.icando.ItemShop.service.UserPointShopService;
import com.icando.global.upload.S3Uploader;
import com.icando.member.entity.ActivityType;
import com.icando.member.entity.Member;
import com.icando.member.entity.PointHistory;
import com.icando.member.entity.Role;
import com.icando.member.repository.MemberRepository;
import com.icando.member.repository.PointRepository;
import com.icando.member.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserPointHistoryShopTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointShopHistoryRepository pointShopHistoryRepository;

    @InjectMocks
    private UserPointShopService userPointShopService;

    @InjectMocks
    private AdminPointShopService adminPointShopService;

    @Mock
    private PointService pointService;

    @Mock
    private S3Uploader s3Uploader;

    private MockMultipartFile imageFile;
    private ItemRequest createItem;
    private ItemRequest createItem2;
    private Member user;
    private String number;

    @BeforeEach
    void setUp() {
        String image = "test.jpg";

        number = "010-1234-1234";
        imageFile = new MockMultipartFile("file", image, "image/jpeg", "dummy image data".getBytes());
        createItem = new ItemRequest("치킨",imageFile,10,100);
        createItem2 = new ItemRequest("피자",imageFile,0,200);
        user = Member.createLocalMemberByTest(
                1L,
                "user1",
                "user@example.com",
                "1234",
                Role.ADMIN,
                false
        );

    }

    @Test
    @DisplayName("상품 구매 성공")
    void buyItem_Success() {
        //given
        when(memberRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Item item = adminPointShopService.createItemByAdminId(createItem, user.getEmail());
        user.addPoints(1000);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        //when
        userPointShopService.buyItem(item.getId(),number, user.getEmail());

        //then
        assertThat(user.getTotalPoint() == 900);
    }

    @Test
    @DisplayName("구매 시 존재하지 않는 상품 예외")
    void buy_Item_Not_Found_Exception() {
        //given
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        //when,then
        PointShopException exception = assertThrows(PointShopException.class, () ->
                userPointShopService.buyItem(999L,number, user.getEmail()));

        assertEquals(PointShopErrorCode.INVALID_ITEM_ID, exception.getErrorCode());
    }

    @Test
    @DisplayName("상품 포인트보다 작을 시 예외")
    void point_less_than_item_point_exception() {
        //given
        when(memberRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Item item = adminPointShopService.createItemByAdminId(createItem, user.getEmail());
        user.addPoints(50);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        //when,then
        PointShopException exception = assertThrows(PointShopException.class, () ->
                userPointShopService.buyItem(item.getId(),number, admin.getEmail()));

        assertEquals(PointShopErrorCode.NOT_ENOUGH_MEMBER_POINT, exception.getErrorCode());
        }

    @Test
    @DisplayName("상품 수량이 없을 시 예외")
    void item_quantity_0_exception() {
        //given
        when(memberRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Item item = adminPointShopService.createItemByAdminId(createItem2, user.getEmail());
        user.addPoints(1000);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        //when,then
        PointShopException exception = assertThrows(PointShopException.class, () ->
                userPointShopService.buyItem(item.getId(),number, admin.getEmail()));

        assertEquals(PointShopErrorCode.OUT_OF_STOCK, exception.getErrorCode());
    }
}


