package com.icando.writing.dto;

import com.icando.feedback.dto.FeedbackResponse;
import com.icando.feedback.entity.Feedback;
import com.icando.member.entity.Member;
import com.icando.writing.entity.Topic;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class WritingListResponse {
    private Long id;
    private FeedbackResponse feedback;
    private TopicResponse topic;
}
