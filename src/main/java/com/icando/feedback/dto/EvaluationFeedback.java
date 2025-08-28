package com.icando.feedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
) {}
