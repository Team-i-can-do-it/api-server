package com.icando.writing.service;

import com.icando.feedback.service.FeedbackService;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import com.icando.member.login.exception.AuthException;
import com.icando.member.repository.MemberRepository;
import com.icando.writing.dto.WritingCreateRequest;
import com.icando.writing.entity.Topic;
import com.icando.writing.entity.Writing;
import com.icando.writing.error.TopicException;
import com.icando.writing.repository.TopicRepository;
import com.icando.writing.repository.WritingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WritingServiceTest {

    @Mock
    private WritingRepository writingRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private WritingService writingService;

    @Test
    @DisplayName("글 생성 성공")
    void createWriting_Success() {
        // given
        Long topicId = 10L;
        String email = "test@test.com";
        String content = "테스트 글 내용입니다.";
        WritingCreateRequest request = new WritingCreateRequest(topicId, content);

        Member mockMember = Member.createLocalMember(
            "testuser",
            email,
            "password",
            Role.USER,
            false
        );
        Topic mockTopic = Topic.of(null, "테스트 주제");
        Writing mockWriting = mock(Writing.class);
        when(mockWriting.getContent()).thenReturn(content);
        when(mockWriting.getTopic()).thenReturn(mockTopic);
        when(mockWriting.getFeedback()).thenReturn(null);
        when(mockWriting.getId()).thenReturn(1L);
        
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(mockTopic));
        when(writingRepository.save(any(Writing.class))).thenReturn(mockWriting);
        when(writingRepository.findById(1L)).thenReturn(Optional.of(mockWriting));


        // when
        writingService.createWriting(request, email);

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
        String email = "test@test.com";
        WritingCreateRequest request = new WritingCreateRequest(10L, "내용");

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThrows(AuthException.class, () -> {
            writingService.createWriting(request, email);
        });

        verify(writingRepository, never()).save(any(Writing.class));
    }

    @Test
    @DisplayName("글 생성 실패 - 주제를 찾을 수 없음")
    void createWriting_Failure_TopicNotFound() {
        // given
        String email = "test@test.com";
        Long topicId = 99L;
        WritingCreateRequest request = new WritingCreateRequest(topicId, "내용");

        Member mockMember = Member.createLocalMember(
            "testuser",
            email,
            "password",
            Role.USER,
            false
        );
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(TopicException.class, () -> {
            writingService.createWriting(request, "test@test.com");
        });

        verify(writingRepository, never()).save(any(Writing.class));
    }
}
