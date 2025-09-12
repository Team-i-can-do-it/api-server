package com.icando.writing.dto;

import com.icando.feedback.dto.FeedbackResponse;
import com.icando.writing.entity.Writing;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WritingResponse {

    private Long id;

    private String content;

    private String topic;

    private FeedbackResponse feedback;
    private LocalDateTime createdAt;

    public static WritingResponse of(Writing writing) {
        WritingResponse response = new WritingResponse();
        response.id = writing.getId();
        response.content = writing.getContent();
        response.topic = writing.getTopic().getTopicContent();
        if (writing.getFeedback() != null) {
            response.feedback = FeedbackResponse.of(writing.getFeedback());
        }
        response.createdAt = writing.getCreatedAt();

        return response;
    }
}
