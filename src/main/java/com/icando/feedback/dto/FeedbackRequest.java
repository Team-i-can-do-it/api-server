package com.icando.feedback.dto;


import com.icando.writing.enums.WritingType;
import jakarta.validation.constraints.NotBlank;

public record FeedbackRequest(
    @NotBlank(message = "타입 정의는 필수입니다.")
    WritingType writingType,

    @NotBlank(message = "글쓰기Id는 필수입니다.")
    Long writingId
) {}
