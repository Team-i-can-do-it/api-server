package com.icando.ItemShop;

import com.icando.ItemShop.dto.ItemRequest;
import com.icando.ItemShop.entity.Item;

import com.icando.ItemShop.exception.PointShopException;
import com.icando.ItemShop.repository.ItemRepository;
import com.icando.ItemShop.repository.PointShopHistoryRepository;
import com.icando.ItemShop.service.AdminPointShopService;
import com.icando.ItemShop.service.UserPointShopService;
import com.icando.global.upload.S3Uploader;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import com.icando.member.repository.MemberRepository;
import com.icando.member.repository.PointHistoryRepository;
import com.icando.member.service.PointService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserPointShopTest {


    @Autowired
    private AdminPointShopService adminPointShopService;

    @Autowired
    private UserPointShopService userPointShopService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointService pointService;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private S3Uploader s3Uploader;

    private MockMultipartFile imageFile;
    private Item item;
    private Long itemId;
    private Member admin;
    @Autowired
    private PointShopHistoryRepository pointShopHistoryRepository;

    @BeforeEach
    void setup() {
        String image = "test.jpg";
        imageFile = new MockMultipartFile("file", image,"image/jpeg", "dummy image data".getBytes());
        when(s3Uploader.upload(any(MockMultipartFile.class), anyString()))
                .thenReturn("https://test-url.com/test.jpg");
        ItemRequest itemDetail = new ItemRequest("치킨",imageFile,2,10);
        admin = Member.createLocalMemberByTest(
                1L,
                "admin",
                "admin@example.com",
                "1234",
                Role.ADMIN
        );
        memberRepository.saveAll(List.of(admin));
        item = adminPointShopService.createItemByAdminId(itemDetail,"admin@example.com");
        itemId = item.getId();
    }

    @Test
    @DisplayName("동시성 환경 100명 동시 상품 잔량 차감 테스트")
    public void buy_1000_together() throws InterruptedException {
        //given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        List<Member> users = new ArrayList<>();
        for (int i=0; i <threadCount; i++) {
            users.add(Member.createLocalMemberByTestByPoint(
                    "user"+ i,
                    "user"+ i +"@example.com",
                    "1234",
                    Role.USER,
                    100
            ));
        }
        memberRepository.saveAll(users);

        //when
        for (int i =0; i < threadCount; i++){
            final int userIndex = i;
            String userEmail = users.get(userIndex).getEmail();
            executorService.execute(()-> {
                try {
                    userPointShopService.buyItem(itemId,"01012345678",userEmail);
                    successCount.incrementAndGet();
                } catch(PointShopException e) {
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        System.out.println("구매 상품 수량:" + successCount.get());
        System.out.println("잔여 상품 수량:" + updatedItem.getQuantity());

        //then
        System.out.println("실패 횟수:" + failCount.get());
        System.out.println("성공 횟수:" + successCount.get());
        assertThat(updatedItem.getQuantity()).isZero();
        assertThat(failCount.get()).isEqualTo(0);
        assertThat(successCount.get()).isEqualTo(100);


    }
}

