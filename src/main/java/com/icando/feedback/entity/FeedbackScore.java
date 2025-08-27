package com.icando.feedback.entity;

import com.icando.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackScore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedbackscore_id")
    private Long id;

    @Column(name = "feedbackscore_feedback_overall_score", nullable = false)
    private Integer feedbackOverallScore;

    @Column(name = "feedbackscore_substance_score", nullable = false)
    private Integer substance_score;

    @Column(name = "feedbackscore_completeness_score", nullable = false)
    private Integer completeness_score;

    @Column(name = "feedbackscore_expressiveness_score", nullable = false)
    private Integer expressiveness_score;

    @Column(name = "feedbackscore_clarity_score", nullable = false)
    private Integer clarity_score;

    @Column(name = "feedbackscore_coherence_score", nullable = false)
    private Integer coherence_score;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;
}
