package com.icando.paragraphCompletion.dto;

import com.icando.feedback.dto.FeedbackResponse;
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
public class ParagraphCompletionResponse {
    private Long id;

    private String content;

    private List<String> words;

    private FeedbackResponse feedback;
    private LocalDateTime createdAt;

    public static ParagraphCompletionResponse of(ParagraphCompletion paragraphCompletion) {
        ParagraphCompletionResponse response = new ParagraphCompletionResponse();
        response.id = paragraphCompletion.getId();
        response.content = paragraphCompletion.getContent();
        response.words = paragraphCompletion.getParagraphWords().stream().map(ParagraphWord::getWord).toList();
        if (paragraphCompletion.getFeedback() != null) {
            response.feedback = FeedbackResponse.of(paragraphCompletion.getFeedback());
        }
        response.createdAt = paragraphCompletion.getCreatedAt();

        return response;
    }
}
