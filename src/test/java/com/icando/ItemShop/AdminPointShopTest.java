package com.icando.ItemShop;

import com.icando.global.upload.S3Uploader;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import com.icando.member.repository.MemberRepository;
import com.icando.ItemShop.dto.ItemRequest;
import com.icando.ItemShop.service.AdminPointShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class AdminPointShopTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
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
            Role.ADMIN,
            false
        );

        //when
        adminPointShopService.createItemByAdminId(createItem,user.getEmail());

        //then
        assertThat(createItem.getName()).isEqualTo("치킨");
        assertThat(createItem.getImageUrl()).isEqualTo(imageFile);
        assertThat(createItem.getQuantity()).isEqualTo(10);
        assertThat(createItem.getPoint()).isEqualTo(100);
        }

}
