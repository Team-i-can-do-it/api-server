package com.icando.feedback.dto;

// 5가지 평가 지표 점수
public record Evaluation(
    Integer substance,
    Integer completeness,
    Integer expressiveness,
    Integer clarity,
    Integer coherence
) {}
