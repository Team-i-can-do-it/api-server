package com.icando.paragraphCompletion.dto;

import com.icando.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ParagraphCompletionRequest {
    @NotBlank
    @Size(min = 100, max = 600, message = "글은 100자 이상 600자 이하로 작성해주세요.")
    private String content;

    @NotEmpty
    private List<String> words;
}
