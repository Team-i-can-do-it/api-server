package com.icando.ItemShop;

import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.repository.ItemRepository;
import com.icando.global.upload.S3Uploader;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import com.icando.member.repository.MemberRepository;
import com.icando.ItemShop.dto.ItemRequest;
import com.icando.ItemShop.service.AdminPointShopService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminPointHistoryShopTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private AdminPointShopService adminPointShopService;

    @Mock
    private S3Uploader s3Uploader;

    private MockMultipartFile imageFile;

    @BeforeEach
    void setUp() {
        String image = "test.jpg";

        imageFile = new MockMultipartFile("file", image,"image/jpeg", "dummy image data".getBytes());
    }

    @Test
    public void 상점_상품_등록_성공() throws Exception {
        //given
        ItemRequest createItem = new ItemRequest("치킨",imageFile,10,100);
        Member user = Member.createLocalMember(
            "user1",
            "user@example.com",
            "1234",
            Role.ADMIN
        );

        when(memberRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(s3Uploader.upload(any(MockMultipartFile.class), anyString()))
                .thenReturn("https://test-url.com/test.jpg");

        //when
        Item item = adminPointShopService.createItemByAdminId(createItem,user.getEmail());

        //then
        assertThat(item.getName()).isEqualTo("치킨");
        assertThat(item.getImageUrl()).isEqualTo("https://test-url.com/test.jpg");
        assertThat(item.getQuantity()).isEqualTo(10);
        assertThat(item.getPoint()).isEqualTo(100);
        }
}
