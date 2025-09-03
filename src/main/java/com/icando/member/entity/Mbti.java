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

    @Column(name = "mbti_iamage_url")
    private String imageUrl;

    private Mbti(
        String name,
        String description,
        String imageUrl
    ) {
        this.name = name;
        this.description = description;
        this.imageUrl = null;
    }

    public Mbti of(String name, String description, String imageUrl) {
        return new Mbti(name, description, imageUrl);
    }
}
