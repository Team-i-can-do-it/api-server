package com.icando.ItemShop;

import com.icando.member.entity.Member;
import com.icando.member.repository.MemberRepository;
import com.icando.ItemShop.dto.CreateItemRequest;
import com.icando.ItemShop.service.AdminPointShopService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class RandomBoxTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AdminPointShopService randomBoxService;

    @Test
    public void 가챠_상품_등록_성공() throws Exception {
        //given
        CreateItemRequest createItem = new CreateItemRequest("치킨","test.image",10,100);
        Member user = Member.of("user1","user@example.com","1234");
        //when
        randomBoxService.createItemByAdminId(createItem,"test.image",user.getId());

        //then
        assertThat(createItem.getName()).isEqualTo("치킨");
        assertThat(createItem.getQuantity()).isEqualTo(10);
        assertThat(createItem.getPoint()).isEqualTo(100);
        }

}
