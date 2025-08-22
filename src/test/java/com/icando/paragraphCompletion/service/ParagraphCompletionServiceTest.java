package com.icando.paragraphCompletion.service;

import com.icando.paragraphCompletion.entity.WordSetItem;
import com.icando.paragraphCompletion.repository.ParagraphCompletionRepository;
import com.icando.paragraphCompletion.repository.WordSetItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParagraphCompletionServiceTest {

    @Mock
    private ParagraphCompletionRepository paragraphCompletionRepository;

    @Mock
    private WordSetItemRepository wordSetItemRepository;

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    private ParagraphCompletionService paragraphCompletionService;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        paragraphCompletionService = new ParagraphCompletionService(
            paragraphCompletionRepository,
            wordSetItemRepository,
            chatClientBuilder
        );
    }

    @Test
    @DisplayName("정상적으로 요청한 개수만큼 단어를 생성한다")
    void generateWords_Success() {
        // given
        int count = 3;
        WordSetItem word1 = createWordSetItem(1, "안녕");
        WordSetItem word2 = createWordSetItem(2, "세상");
        WordSetItem word3 = createWordSetItem(3, "테스트");

        when(wordSetItemRepository.getRandomWords(anyInt()))
            .thenReturn(List.of(word1, word2, word3));

        // when
        List<String> result = paragraphCompletionService.generateWords(count);

        // then
        assertEquals(count, result.size());
        assertEquals("안녕", result.get(0));
        assertEquals("세상", result.get(1));
        assertEquals("테스트", result.get(2));
        verify(wordSetItemRepository, times(1)).getRandomWords(anyInt());
    }

    @Test
    @DisplayName("0개 요청 시 빈 리스트를 반환한다")
    void generateWords_ZeroCount() {
        // given
        int count = 0;

        // when
        List<String> result = paragraphCompletionService.generateWords(count);

        // then
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
        verify(wordSetItemRepository, times(1)).getRandomWords(anyInt());
    }

    @Test
    @DisplayName("단일 단어 생성 테스트")
    void generateWords_SingleWord() {
        // given
        int count = 1;
        WordSetItem word = createWordSetItem(1, "단어");

        when(wordSetItemRepository.getRandomWords(anyInt())).thenReturn(List.of(word));

        // when
        List<String> result = paragraphCompletionService.generateWords(count);

        // then
        assertEquals(1, result.size());
        assertEquals("단어", result.get(0));
        verify(wordSetItemRepository, times(1)).getRandomWords(anyInt());
    }

    private WordSetItem createWordSetItem(int id, String word) {
        // WordSetItem을 더 정확하게 모킹하기 위해 개별 객체로 생성
        WordSetItem wordSetItem = mock(WordSetItem.class);
//        when(wordSetItem.getId()).thenReturn(id);
        when(wordSetItem.getWord()).thenReturn(word);
        return wordSetItem;
    }
}