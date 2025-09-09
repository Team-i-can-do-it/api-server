package com.icando.feedback.service;

import com.icando.feedback.dto.FeedbackRequest;
import com.icando.feedback.dto.FeedbackResponse;
import com.icando.feedback.dto.MbtiScore;
import com.icando.feedback.entity.Feedback;
import com.icando.feedback.entity.FeedbackScore;
import com.icando.feedback.exception.FeedbackErrorCode;
import com.icando.feedback.exception.FeedbackException;
import com.icando.feedback.repository.FeedbackRepository;
import com.icando.feedback.repository.FeedbackScoreRepository;
import com.icando.member.entity.ActivityType;
import com.icando.member.service.PointService;
import com.icando.member.repository.MbtiRepository;
import com.icando.paragraphCompletion.entity.ParagraphCompletion;
import com.icando.paragraphCompletion.entity.ParagraphWord;
import com.icando.paragraphCompletion.repository.ParagraphCompletionRepository;
import com.icando.writing.entity.Topic;
import com.icando.writing.entity.Writing;
import com.icando.writing.enums.WritingType;
import com.icando.writing.service.WritingService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final ChatClient.Builder chatClientBuilder;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackScoreRepository feedbackScoreRepository;
    private final WritingService writingService;
    private final MbtiRepository mbtiRepository;
    private final PointService pointService;
    private final ParagraphCompletionRepository paragraphCompletionRepository;

    @Value("${feedback.evaluation.prompt.feedback}")
    private String evaluationPromptFeedback;

    // TODO: 추후 stream, Flux 비동기로 성능개선
    @Transactional
    public FeedbackResponse generateFeedback(FeedbackRequest request, ActivityType activityType) {
        if (request.writingType() == WritingType.WRITING) {
            return generateWritingFeedback(request, activityType);
        } else {
            return generateParagraphCompletionFeedback(request, activityType);
        }
    }

    private FeedbackResponse generateParagraphCompletionFeedback(FeedbackRequest request, ActivityType activityType) {
        ParagraphCompletion paragraphCompletion = paragraphCompletionRepository.findById(request.writingId())
                .orElseThrow(() -> new FeedbackException(FeedbackErrorCode.WRITING_NOT_FOUND));
        String topic = paragraphCompletion.getParagraphWords().stream().map(ParagraphWord::getWord).collect(Collectors.joining(", "));

        // AI에 요청후 JSON 응답을 DTO로 변환
        FeedbackResponse aiResponse = chatClientBuilder.build()
                .prompt("다음은 유저가 선택한 단어 목록입니다. " + topic)
                .system(evaluationPromptFeedback)
                .user(paragraphCompletion.getContent())
                .call()
                .entity(FeedbackResponse.class);

        if (aiResponse == null) {
            throw new FeedbackException(FeedbackErrorCode.FEEDBACK_GENERATION_FAILED);
        }

        pointService.earnPoints(paragraphCompletion.getMember().getId(),100,activityType);
        Feedback savedFeedback = saveFeedback(aiResponse);
        paragraphCompletion.updateFeedback(savedFeedback);
        saveFeedbackScore(aiResponse, savedFeedback);
        return aiResponse;
    }

    private FeedbackResponse generateWritingFeedback(FeedbackRequest request, ActivityType activityType) {
        Writing writing = writingService.getWriting(request.writingId());
        Topic topic = writing.getTopic();

        // AI에 요청후 JSON 응답을 DTO로 변환
        FeedbackResponse aiResponse = chatClientBuilder.build()
                .prompt("다음은 유저가 선택한 주제입니다." + topic)
                .system(evaluationPromptFeedback)
                .user(writing.getContent())
                .call()
                .entity(FeedbackResponse.class);

        if (aiResponse == null) {
            throw new FeedbackException(FeedbackErrorCode.FEEDBACK_GENERATION_FAILED);
        }

        pointService.earnPoints(writing.getMember().getId(),100,activityType);
        Feedback savedFeedback = saveFeedback(aiResponse);
        writing.updateFeedback(savedFeedback);
        saveFeedbackScore(aiResponse, savedFeedback);
        return aiResponse;
    }

    private Feedback saveFeedback(FeedbackResponse aiResponse) {
        Feedback feedbackToSave = Feedback.builder()
            .content(aiResponse.overallFeedback())
            .score(aiResponse.overallScore())
            .expressionStyle(aiResponse.mbtiScore().expressionStyle())
            .contentFormat(aiResponse.mbtiScore().contentFormat())
            .toneOfVoice(aiResponse.mbtiScore().toneOfVoice())
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
