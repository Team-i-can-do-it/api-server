package com.icando.feedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FeedbackResponse(
    Mbti mbti,
    Evaluation evaluation,
    @JsonProperty("evaluation_feedback")
    EvaluationFeedback evaluationFeedback,
    @JsonProperty("overall_feedback")
    String overallFeedback,
    @JsonProperty("overall_score")
    Integer overallScore
) { }
