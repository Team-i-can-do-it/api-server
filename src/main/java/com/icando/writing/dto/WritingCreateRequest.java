package com.icando.writing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import javax.validation.constraints.NotNull;

public record WritingCreateRequest(
    @NotNull(message = "주제 ID는 필수입니다.")
    Long topicId,

    @Size(min = 100, max = 600, message = "글자는 최소 100자에서 600자입니다.")
    @NotBlank(message = "글 내용은 필수입니다.")
    String content
) {
}
