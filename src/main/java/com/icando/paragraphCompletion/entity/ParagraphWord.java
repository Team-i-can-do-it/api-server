package com.icando.paragraphCompletion.entity;

import com.icando.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParagraphWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paragraph_word_id")
    private Long id;

    @Column
    private String word;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="paragraph_completion_id" , nullable = false)
    private ParagraphCompletion paragraphCompletion;
}
