package com.icando.feedback.dto;


import com.icando.writing.enums.WritingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FeedbackRequest(
    @NotNull(message = "타입 정의는 필수입니다.")
    WritingType writingType,

    @NotNull(message = "글쓰기 ID는 필수입니다.")
    @Min(value = 1, message = "글쓰기 ID는 1 이상의 값이어야 합니다.")
    Long writingId
) {}
