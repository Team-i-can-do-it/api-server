package com.icando.member.entity;

import com.icando.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_email")
    private String email;

    @Column(name = "member_password")
    private String password;

    @Column(name = "member_provider")
    private String provider;

    @Column(name = "member_provider_id")
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.ROLE_USER;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="mbti_id")
    private Mbti mbti;

    private Member(String name, String email,String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static Member of(String name, String email,String password) {
        return new Member(name,email,password);
    }
}
