package com.icando.writing.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.icando.member.entity.Member;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import com.icando.writing.dto.WritingCreateRequest;
import com.icando.writing.entity.Topic;
import com.icando.writing.entity.Writing;
import com.icando.writing.error.TopicException;
import com.icando.writing.repository.TopicRepository;
import com.icando.writing.repository.WritingRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WritingServiceTest {

    @Mock
    private WritingRepository writingRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private WritingService writingService;

    @Test
    @DisplayName("글 생성 성공")
    void createWriting_Success() {
        // given
        Long memberId = 1L;
        Long topicId = 10L;
        String content = "테스트 글 내용입니다.";
        WritingCreateRequest request = new WritingCreateRequest(topicId, content);

        Member mockMember = Member.of("testuser", "test@test.com", "password");
        Topic mockTopic = new Topic(null, "테스트 주제");
        
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(mockTopic));

        // when
        writingService.createWriting(request, memberId);

        // then
        ArgumentCaptor<Writing> writingCaptor = ArgumentCaptor.forClass(Writing.class);
        verify(writingRepository).save(writingCaptor.capture());

        Writing savedWriting = writingCaptor.getValue();
        assertEquals(content, savedWriting.getContent());
        assertEquals(mockMember, savedWriting.getMember());
        assertEquals(mockTopic, savedWriting.getTopic());
    }

    @Test
    @DisplayName("글 생성 실패 - 사용자를 찾을 수 없음")
    void createWriting_Failure_MemberNotFound() {
        // given
        Long memberId = 99L;
        WritingCreateRequest request = new WritingCreateRequest(10L, "내용");

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberException.class, () -> {
            writingService.createWriting(request, memberId);
        });

        verify(writingRepository, never()).save(any(Writing.class));
    }

    @Test
    @DisplayName("글 생성 실패 - 주제를 찾을 수 없음")
    void createWriting_Failure_TopicNotFound() {
        // given
        Long memberId = 1L;
        Long topicId = 99L;
        WritingCreateRequest request = new WritingCreateRequest(topicId, "내용");

        Member mockMember = Member.of("testuser", "test@test.com", "password");
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(TopicException.class, () -> {
            writingService.createWriting(request, memberId);
        });

        verify(writingRepository, never()).save(any(Writing.class));
    }
}
