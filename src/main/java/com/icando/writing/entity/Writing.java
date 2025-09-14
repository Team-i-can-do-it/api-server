package com.icando.writing.entity;

import com.icando.feedback.entity.Feedback;
import com.icando.global.BaseEntity;
import com.icando.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Writing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "writing_id")
    private Long id;

    @Column(name = "writing_content", length = 600)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id" , nullable = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="feedback_id" , nullable = true)
    private Feedback feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    public static Writing of(String content, Member member, Topic topic) {

        Writing writing = new Writing();

        writing.content = content;
        writing.member = member;
        writing.topic = topic;

        return writing;
    }

    /**
     * Writing에서 생성된 피드백을 연결하는 메서드
     * @param feedback 생성된 피드백 엔티티
     */
    public void updateFeedback(Feedback feedback) {
        this.feedback = feedback;
    }
}
