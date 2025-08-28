package com.icando.feedback.dto;

import jakarta.validation.constraints.NotBlank;

public record FeedbackRequest(
    @NotBlank(message = "피드백할 내용은 필수입니다.")
    String content,
    @NotBlank(message = "글의 주제는 필수입니다.")
    String topic
) {}
