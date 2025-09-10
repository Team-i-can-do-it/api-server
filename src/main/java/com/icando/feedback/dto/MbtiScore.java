package com.icando.feedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icando.feedback.entity.Feedback;

// MBTI 점수
public record MbtiScore(
    @JsonProperty("expression_style")
    Integer expressionStyle,
    @JsonProperty("content_format")
    Integer contentFormat,
    @JsonProperty("tone_of_voice")
    Integer toneOfVoice
) {
    public static MbtiScore of(Feedback feedback) {
        return new MbtiScore(
            feedback.getExpressionStyle(),
            feedback.getContentFormat(),
            feedback.getToneOfVoice()
        );
    }
}
