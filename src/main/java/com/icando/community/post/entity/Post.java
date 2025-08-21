package com.icando.community.post.entity;

import com.icando.global.BaseEntity;
import com.icando.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name ="post_title")
    private String title;

    @Column(name ="post_content")
    private String content;

    @Column(name ="post_view_count")
    private int viewCount;

    @Column(name ="post_content_count")
    private int contentCount;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id" , nullable = false)
    private Member member;
}
