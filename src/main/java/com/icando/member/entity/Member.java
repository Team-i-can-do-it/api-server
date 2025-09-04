package com.icando.member.entity;

import com.icando.global.BaseEntity;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "member_provider")
    private Provider provider;

    @Column(name = "member_provider_id")
    private String providerId;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="mbti_id")
    private Mbti mbti;

    @Column(name = "member_total_point")
    @ColumnDefault("0")
    private int totalPoint;

    private Member(String name, String email,String password,Provider provider,
                   String providerId, Boolean isVerified, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
        this.isVerified = isVerified;
        this.role = role;
    }
    private Member(Long id ,String name, String email,String password,Provider provider,
                    String providerId, Boolean isVerified, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
        this.isVerified = isVerified;
        this.role = role;
    }

    //로컬 자체로그인 회원 객체 생성
    public static Member createLocalMember(String name, String email,String password, Role role, Boolean isVerified) {
        return new Member(name, email, password, null, null, false, role);
    }

    public static Member createLocalMemberByTest(Long id,String name, String email,String password, Role role, Boolean isVerified) {

        return new Member(id, name, email, password, null, null, false, role);
    }

    public void updateVerify() {
        this.isVerified = true;
    }

    public void addPoints(int getPoint) {
        totalPoint += getPoint;
    }

    public void decreasePoint(int itemPoint){
        if(totalPoint < itemPoint){
            throw new MemberException(MemberErrorCode.NOT_ENOUGH_POINTS);}
        totalPoint -= itemPoint;
    }
}


