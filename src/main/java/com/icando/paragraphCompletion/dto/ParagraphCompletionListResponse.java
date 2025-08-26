package com.icando.paragraphCompletion.dto;

import com.icando.paragraphCompletion.entity.ParagraphCompletion;
import com.icando.paragraphCompletion.entity.ParagraphWord;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ParagraphCompletionListResponse {
    private Long id;

    private List<String> words;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public static ParagraphCompletionListResponse of(ParagraphCompletion paragraphCompletion) {
        ParagraphCompletionListResponse response = new ParagraphCompletionListResponse();
        response.id = paragraphCompletion.getId();
        response.words = paragraphCompletion.getParagraphWords().stream().map(ParagraphWord::getWord).toList();
        response.createdAt = paragraphCompletion.getCreatedAt();
        response.modifiedAt = paragraphCompletion.getModifiedAt();
        return response;
    }
}
