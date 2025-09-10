package com.icando.feedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icando.feedback.entity.Feedback;

import java.time.LocalDateTime;

public record FeedbackResponse(
    MbtiScore mbtiScore,
    Evaluation evaluation,
    @JsonProperty("evaluation_feedback")
    EvaluationFeedback evaluationFeedback,
    @JsonProperty("overall_feedback")
    String overallFeedback,
    @JsonProperty("overall_score")
    Integer overallScore,
    LocalDateTime createdAt
) {
    public static FeedbackResponse of(Feedback feedback) {
        return new FeedbackResponse(
            MbtiScore.of(feedback),
            Evaluation.of(feedback.getFeedbackScore()),
            EvaluationFeedback.of(feedback),
            feedback.getContent(),
            feedback.getFeedbackScore().getFeedbackOverallScore(),
            feedback.getCreatedAt()
        );
    }
}
