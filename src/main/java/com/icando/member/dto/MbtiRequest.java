package com.icando.member.dto;

import jakarta.validation.constraints.NotBlank;

public record MbtiRequest(
    @NotBlank(message = "캐릭터의 이름은 필수입니다.")
    String name,
    @NotBlank(message = "캐릭터의 설명은 필수입니다.")
    String description,
    @NotBlank(message = "캐릭터의 이미지링크는 필수입니다.")
    String imageUrl
) {
}
