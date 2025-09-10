package com.icando.feedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icando.feedback.entity.Feedback;

// 5가지 평가 지표 피드백
public record EvaluationFeedback(
    @JsonProperty("substance_feedback")
    String substanceFeedback,
    @JsonProperty("completeness_feedback")
    String completenessFeedback,
    @JsonProperty("expressiveness_feedback")
    String expressivenessFeedback,
    @JsonProperty("clarity_feedback")
    String clarityFeedback,
    @JsonProperty("coherence_feedback")
    String coherenceFeedback
) {
    public static EvaluationFeedback of(Feedback feedback) {
        return new EvaluationFeedback(
            feedback.getSubstance(),
            feedback.getCompleteness(),
            feedback.getExpressiveness(),
            feedback.getClarity(),
            feedback.getCoherence()
        );
    }
}
