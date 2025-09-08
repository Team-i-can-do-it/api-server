package com.icando.member.entity;

import com.icando.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mbti extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mbti_id")
    private Long id;

    @Column(name = "mbti_name")
    private String name;

    @Column(name = "mbti_description")
    private String description;

    @Column(name = "mbti_image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id")
    private Member member;

    private Mbti(
        String name,
        String description,
        String imageUrl
    ) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static Mbti of(String name, String description, String imageUrl) {
        return new Mbti(name, description, imageUrl);
    }

    public void updateMember(Member member) {
        this.member = member;
    }

}
