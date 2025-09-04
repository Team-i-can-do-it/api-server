package com.icando.feedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icando.feedback.entity.Feedback;

public record FeedbackResponse(
    MbtiScore mbtiScore,
    Evaluation evaluation,
    @JsonProperty("evaluation_feedback")
    EvaluationFeedback evaluationFeedback,
    @JsonProperty("overall_feedback")
    String overallFeedback,
    @JsonProperty("overall_score")
    Integer overallScore
) {
}
