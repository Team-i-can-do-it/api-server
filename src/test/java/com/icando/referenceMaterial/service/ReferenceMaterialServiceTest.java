package com.icando.referenceMaterial.service;

import com.icando.referenceMaterial.dto.ReferenceMaterialListResponse;
import com.icando.referenceMaterial.entity.ReferenceMaterial;
import com.icando.referenceMaterial.enums.ReferenceMaterialErrorCode;
import com.icando.referenceMaterial.exception.ReferenceMaterialException;
import com.icando.referenceMaterial.repository.ReferenceMaterialRepository;
import com.icando.writing.entity.Topic;
import com.icando.writing.repository.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferenceMaterialServiceTest {
    @InjectMocks
    private ReferenceMaterialService referenceMaterialService;

    @Mock
    private ChatClient.Builder ai;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private ReferenceMaterialRepository referenceMaterialRepository;

    @Test
    @DisplayName("주제 ID로 참고 자료 조회 - 성공")
    void getReferenceMaterialsByTopicId_Success() {
        // Given
        Long topicId = 1L;

        when(topicRepository.findById(anyLong())).thenReturn(Optional.of(mock(Topic.class)));
        when(referenceMaterialRepository.findAllByTopic(any(Topic.class)))
                .thenReturn(List.of(mock(ReferenceMaterial.class), mock(ReferenceMaterial.class)));

        //when
        List<ReferenceMaterialListResponse> responses = referenceMaterialService.getReferenceMaterialsByTopicId(topicId);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        verify(topicRepository, times(1)).findById(anyLong());
        verify(referenceMaterialRepository, times(1)).findAllByTopic(any(Topic.class));
    }

    @Test
    @DisplayName("주제 ID로 참고 자료 조회 - 주제 없음")
    void getReferenceMaterialsByTopicId_TopicNotFound() {
        // Given
        Long topicId = 1L;

        when(topicRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(ReferenceMaterialException.class, () -> {
            referenceMaterialService.getReferenceMaterialsByTopicId(topicId);
        });

        assertEquals(ReferenceMaterialErrorCode.TOPIC_NOT_FOUND.getMessage(), exception.getMessage());

        verify(topicRepository, times(1)).findById(anyLong());
        verify(referenceMaterialRepository, never()).findAllByTopic(any(Topic.class));
    }
}