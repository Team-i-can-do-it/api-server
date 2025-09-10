package com.icando.feedback.entity;

import com.icando.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(name = "feedback_content")
    private String content;

    @Column(name = "feedback_expression_style")
    private Integer expressionStyle;

    @Column(name = "feedback_content_format")
    private Integer contentFormat;

    @Column(name = "feedback_tone_of_voice")
    private Integer toneOfVoice;

    @Column(name = "feedback_substance")
    private String substance;

    @Column(name = "feedback_completeness")
    private String completeness;

    @Column(name = "feedback_expressiveness")
    private String expressiveness;

    @Column(name = "feedback_clarity")
    private String clarity;

    @Column(name = "feedback_coherence")
    private String coherence;

    @OneToOne(mappedBy = "feedback", fetch = FetchType.LAZY)
    private FeedbackScore feedbackScore;

    @Builder
    public Feedback(
        String content,
        Integer expressionStyle,
        Integer contentFormat,
        Integer toneOfVoice,
        String substance,
        String completeness,
        String expressiveness,
        String clarity,
        String coherence
    ) {
        this.content = content;
        this.expressionStyle = expressionStyle;
        this.contentFormat = contentFormat;
        this.toneOfVoice = toneOfVoice;
        this.substance = substance;
        this.completeness = completeness;
        this.expressiveness = expressiveness;
        this.clarity = clarity;
        this.coherence = coherence;
    }
}
