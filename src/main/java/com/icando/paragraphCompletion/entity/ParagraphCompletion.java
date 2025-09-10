package com.icando.paragraphCompletion.entity;

import com.icando.feedback.entity.Feedback;
import com.icando.global.BaseEntity;
import com.icando.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParagraphCompletion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paragraph_completion_id")
    private Long id;

    @Column(name = "paragraph_completion_content")
    private String content;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id" , nullable = false)
    private Member member;

    @OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name ="feedback_id")
    private Feedback feedback;

    @OneToMany(mappedBy = "paragraphCompletion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParagraphWord> paragraphWords = new ArrayList<>();

    public static ParagraphCompletion of(@NotBlank @Size(max = 600, message = "글은 600자 이하로 작성해주세요.") String content, Member member) {
        ParagraphCompletion paragraphCompletion = new ParagraphCompletion();
        paragraphCompletion.content = content;
        paragraphCompletion.member = member;
        return paragraphCompletion;
    }

    public void updateFeedback(Feedback savedFeedback) {
        this.feedback = savedFeedback;
    }
}
