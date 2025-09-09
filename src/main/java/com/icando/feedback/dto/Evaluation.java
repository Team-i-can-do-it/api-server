package com.icando.feedback.dto;

import com.icando.feedback.entity.FeedbackScore;

// 5가지 평가 지표 점수
public record Evaluation(
    Integer substance,
    Integer completeness,
    Integer expressiveness,
    Integer clarity,
    Integer coherence
) {
    public static Evaluation of(FeedbackScore feedbackScore) {
        return new Evaluation(
            feedbackScore.getSubstance_score(),
            feedbackScore.getCompleteness_score(),
            feedbackScore.getExpressiveness_score(),
            feedbackScore.getClarity_score(),
            feedbackScore.getCoherence_score()
        );
    }
}
