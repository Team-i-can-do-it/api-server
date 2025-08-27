package com.icando.feedback.service;

import com.icando.feedback.dto.FeedbackReqeust;
import com.icando.feedback.dto.FeedbackResponse;
import com.icando.feedback.entity.Feedback;
import com.icando.feedback.entity.FeedbackScore;
import com.icando.feedback.exception.FeedbackErrorCode;
import com.icando.feedback.exception.FeedbackException;
import com.icando.feedback.repository.FeedbackRepository;
import com.icando.feedback.repository.FeedbackScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final ChatClient.Builder chatClientBuilder;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackScoreRepository feedbackScoreRepository;

    @Value("${feedback.evaluation.prompt.feedback}")
    private String evaluationPromptFeedback;

    @Transactional
    public FeedbackResponse generateFeedback(FeedbackReqeust reqeust) {
        // 1. AI에 요청후 JSON 응답을 DTO로 변환
        FeedbackResponse aiResponse = chatClientBuilder.build()
                .prompt("다음은 유저가 선택한 주제입니다." + reqeust.topic())
                .system(evaluationPromptFeedback)
                .user(reqeust.content())
                .call()
                .entity(FeedbackResponse.class);

        if (aiResponse == null) {
            throw new FeedbackException(FeedbackErrorCode.FEEDBACK_GENERATION_FAILED);
        }

        // 2. Feedback 엔티티 생성 및 저장
        Feedback savedFeedback = saveFeedback(aiResponse);

        // 3. FeedbackScore 엔티티 생성 및 저장
        saveFeedbackScore(aiResponse, savedFeedback);

        // 4. DTO 반환
        return aiResponse;
    }

    private Feedback saveFeedback(FeedbackResponse aiResponse) {
        Feedback feedbackToSave = Feedback.builder()
            .content(aiResponse.overallFeedback())
            .score(aiResponse.overallScore())
            .expressionStyle(aiResponse.mbti().expressionStyle())
            .contentFormat(aiResponse.mbti().contentFormat())
            .toneOfVoice(aiResponse.mbti().toneOfVoice())
            .substance(aiResponse.evaluationFeedback().substanceFeedback())
            .completeness(aiResponse.evaluationFeedback().completenessFeedback())
            .expressiveness(aiResponse.evaluationFeedback().expressivenessFeedback())
            .clarity(aiResponse.evaluationFeedback().clarityFeedback())
            .coherence(aiResponse.evaluationFeedback().coherenceFeedback())
            .build();

        return feedbackRepository.save(feedbackToSave);
    }

    private void saveFeedbackScore(FeedbackResponse aiResponse, Feedback savedFeedback) {
        FeedbackScore scoreToSave = FeedbackScore.builder()
            .feedbackOverallScore(aiResponse.overallScore())
            .substance_score(aiResponse.evaluation().substance())
            .completeness_score(aiResponse.evaluation().completeness())
            .expressiveness_score(aiResponse.evaluation().expressiveness())
            .clarity_score(aiResponse.evaluation().clarity())
            .coherence_score(aiResponse.evaluation().coherence())
            .feedback(savedFeedback)
            .build();

        feedbackScoreRepository.save(scoreToSave);
    }
}
