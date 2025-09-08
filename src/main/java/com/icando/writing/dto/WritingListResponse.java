package com.icando.writing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icando.feedback.dto.FeedbackResponse;
import com.icando.feedback.entity.Feedback;
import com.icando.member.entity.Member;
import com.icando.writing.entity.Topic;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WritingListResponse {
    private Long id;
    private String topic;
    private String summary;
    Integer expressionStyle;
    Integer contentFormat;
    Integer toneOfVoice;
    Integer score;
    private LocalDateTime createdAt;
}
