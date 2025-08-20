package com.icando.member.entity;

import com.icando.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @Column(name = "profile_nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_gender")
    private Gender gender;

    @Column(name = "profile_job")
    private String job;

    @Column(name = "profile_intro")
    private String intro;

    @OneToOne (fetch = FetchType.LAZY ,cascade = CascadeType.PERSIST)
    @JoinColumn(name ="member_id" , nullable = false)
    private Member member;

}
