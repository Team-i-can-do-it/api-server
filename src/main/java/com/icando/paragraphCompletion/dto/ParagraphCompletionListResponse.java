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
    private String summary;
    Integer expressionStyle;
    Integer contentFormat;
    Integer toneOfVoice;
    Integer score;
    private LocalDateTime createdAt;

    public static ParagraphCompletionListResponse of(ParagraphCompletion paragraphCompletion) {
        ParagraphCompletionListResponse response = new ParagraphCompletionListResponse();
        response.id = paragraphCompletion.getId();
        response.words = paragraphCompletion.getParagraphWords().stream().map(ParagraphWord::getWord).toList();
        response.summary = (paragraphCompletion.getContent().length() > 200 ? paragraphCompletion.getContent().substring(0, 200) + "..." : paragraphCompletion.getContent());
        if (paragraphCompletion.getFeedback() != null) {
            response.expressionStyle = paragraphCompletion.getFeedback().getExpressionStyle();
            response.contentFormat = paragraphCompletion.getFeedback().getContentFormat();
            response.toneOfVoice = paragraphCompletion.getFeedback().getToneOfVoice();
            response.score = paragraphCompletion.getFeedback().getFeedbackScore().getFeedbackOverallScore();
        }
        response.createdAt = paragraphCompletion.getCreatedAt();
        return response;
    }
}
