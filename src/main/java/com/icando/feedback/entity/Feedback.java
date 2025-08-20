package com.icando.feedback.entity;

import com.icando.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @Column(name ="feedback_content")
    private String content;

    @Column(name = "feedback_score")
    private int score;

    @Column(name = "feedback_expression_style")
    private String expressionStyle;

    @Column(name = "feedback_content_format")
    private String contentFormat;

    @Column(name = "feedback_tone_of_voice")
    private String toneOfVoice;

}
