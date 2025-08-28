package com.icando.randomBox;

import com.icando.member.entity.Member;
import com.icando.member.repository.MemberRepository;
import com.icando.randomBox.dto.CreateItemRequest;
import com.icando.randomBox.entity.RandomBox;
import com.icando.randomBox.service.RandomBoxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class RandomBoxTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RandomBoxService randomBoxService;

    @Test
    public void 가챠_상품_등록_성공() throws Exception {
        //given
        CreateItemRequest createItem = new CreateItemRequest("치킨",10,2.5f);
        Member user = Member.of("user1","user@example.com","1234");
        //when
        randomBoxService.createItemByAdminId(createItem,user.getId());

        //then
        assertThat(createItem.getItem()).isEqualTo("치킨");
        assertThat(createItem.getQuantity()).isEqualTo(10);
        assertThat(createItem.getProbability()).isEqualTo(2.5f);
        }

}
