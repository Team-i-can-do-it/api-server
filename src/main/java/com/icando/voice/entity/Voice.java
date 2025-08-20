package com.icando.voice.entity;

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
public class Voice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voice_id")
    private Long id;

    @Column(name = "voice_content")
    private String content;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id" , nullable = false)
    private Member member;

    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="feedback_id" , nullable = false)
    private Feedback feedback;

}
