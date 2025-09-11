package com.icando.ItemShop;

import com.icando.ItemShop.dto.ItemRequest;
import com.icando.ItemShop.entity.Item;

import com.icando.ItemShop.repository.ItemRepository;
import com.icando.ItemShop.service.AdminPointShopService;
import com.icando.ItemShop.service.UserPointShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
public class UserPointShopTest {


    @Autowired
    private AdminPointShopService adminPointShopService;

    @Autowired
    private UserPointShopService userPointShopService;

    @Autowired
    private ItemRepository itemRepository;

    private MockMultipartFile imageFile;
    private Item item;

    @BeforeEach
    void setup() {
        String image = "test.jpg";
        imageFile = new MockMultipartFile("file", image,"image/jpeg", "dummy image data".getBytes());
        ItemRequest itemDetail = new ItemRequest("치킨",imageFile,10001,10);
        item = adminPointShopService.createItemByAdminId(itemDetail,"test@test.com");
    }

    @Test
    @DisplayName("동시성 환경 10000명 쿠폰 차감 테스트")
    public void buy_10000_together() throws Exception {
        //given
        int threadCount = 10000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        //when
        for (int i =0; i < threadCount; i++){
            executorService.submit(()-> {
                try {
                    userPointShopService.buyItem(1L,"01012345678","test2@test.com");
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        //then
        assertThat(item.getQuantity()).isNotZero();
        System.out.println("잔여 상품 수량:" + item.getQuantity());

        }
}
