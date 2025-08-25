package com.icando.paragraphCompletion.dto;

import com.icando.member.entity.Member;
import com.icando.paragraphCompletion.entity.ParagraphCompletion;
import com.icando.paragraphCompletion.entity.ParagraphWord;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ParagraphCompletionResponse {
    private Long id;

    private String content;

    private List<String> words;

    // TODO: Feedback DTO 추가해야함

    public static ParagraphCompletionResponse of(ParagraphCompletion paragraphCompletion) {
        ParagraphCompletionResponse response = new ParagraphCompletionResponse();
        response.id = paragraphCompletion.getId();
        response.content = paragraphCompletion.getContent();
        response.words = paragraphCompletion.getParagraphWords().stream().map(ParagraphWord::getWord).toList();
        return response;
    }
}
