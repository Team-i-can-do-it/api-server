package com.icando.writing.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.icando.writing.entity.Topic;
import com.icando.writing.enums.Category;
import com.icando.writing.repository.TopicRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TopicService topicService;

    @Test
    @DisplayName("카테고리별 랜덤 주제 조회 성공")
    void getRandomTopicByCategory_Success() {
        // given
        Category category = Category.DAILY_LIFE;
        Topic expectedTopic = Topic.of(category, "테스트 주제");

        when(topicRepository.findRandomByCategory(category.name()))
            .thenReturn(Optional.of(expectedTopic));

        // when
        Topic actualTopic = topicService.getRandomTopicByCategory(category);

        // then
        assertNotNull(actualTopic);
        assertEquals(expectedTopic.getTopicContent(), actualTopic.getTopicContent());
        verify(topicRepository).findRandomByCategory(category.name());
    }

    @Test
    @DisplayName("전체 랜덤 주제 조회 성공")
    void getRandomTopic_Success() {
        // given
        Topic expectedTopic = Topic.of(Category.DAILY_LIFE, "전체 랜덤 테스트 주제");
        when(topicRepository.findRandom()).thenReturn(Optional.of(expectedTopic));

        // when
        Topic actualTopic = topicService.getRandomTopic();

        // then
        assertNotNull(actualTopic);
        assertEquals(expectedTopic.getTopicContent(), actualTopic.getTopicContent());
        verify(topicRepository).findRandom();
    }
}
