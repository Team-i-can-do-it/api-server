package com.icando.feedback.service;

import com.icando.feedback.dto.*;
import com.icando.feedback.entity.Feedback;
import com.icando.feedback.entity.FeedbackScore;
import com.icando.feedback.repository.FeedbackRepository;
import com.icando.feedback.repository.FeedbackScoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // JUnit5에서 Mockito를 사용하기 위한 설정
class FeedbackServiceTest {

    @InjectMocks
    private FeedbackService feedbackService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private FeedbackScoreRepository feedbackScoreRepository;

    @Test
    @DisplayName("피드백 생성 및 저장 로직 성공 테스트")
    void generateFeedback_success() {
        // Given
        FeedbackReqeust request = new FeedbackReqeust("테스트 주제", "테스트 콘텐츠");
        FeedbackResponse mockAiResponse = createMockFeedbackResponse(); // 가짜 응답

        when(chatClientBuilder.build()
            .prompt(anyString())
            .system((String) any())
            .user(request.content())
            .call()
            .entity(FeedbackResponse.class))
        .thenReturn(mockAiResponse);

        // When
        FeedbackResponse actualResponse = feedbackService.generateFeedback(request);

        // Then
        assertThat(actualResponse.overallFeedback()).isEqualTo("전반적으로 훌륭합니다!");
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
        verify(feedbackScoreRepository, times(1)).save(any(FeedbackScore.class));
    }

    private FeedbackResponse createMockFeedbackResponse() {
        // 1. 중첩 DTO 객체들을 먼저 생성합니다.
        Mbti mbti = new Mbti(
            -50,
            50,
            10
        );

        Evaluation evaluation = new Evaluation(
            90,
            92,
            93,
            94,
            96
        );

        EvaluationFeedback evaluationFeedback = new EvaluationFeedback(
            "내용 좋음",
            "완성도 높음",
            "표현력 좋음",
            "명료함",
            "일관성 있음"
        );

        return new FeedbackResponse(
            mbti,
            evaluation,
            evaluationFeedback,
            "전반적으로 훌륭합니다!",
            95
        );
    }
    
}
