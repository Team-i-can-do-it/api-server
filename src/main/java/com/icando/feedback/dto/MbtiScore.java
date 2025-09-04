package com.icando.feedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// MBTI 점수
public record MbtiScore(
    @JsonProperty("expression_style")
    Integer expressionStyle,
    @JsonProperty("content_format")
    Integer contentFormat,
    @JsonProperty("tone_of_voice")
    Integer toneOfVoice
) {}
