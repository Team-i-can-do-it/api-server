package com.icando.randomBox.entity;

import com.icando.global.BaseEntity;
import com.icando.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RandomBoxHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "random_box_history_id")
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id" , nullable = false)
    private Member member;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="random_box_id")
    private RandomBox randomBox;
}
