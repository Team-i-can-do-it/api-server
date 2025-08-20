package com.icando.community.comment.entity;

import com.icando.community.post.entity.Post;
import com.icando.global.BaseEntity;
import com.icando.member.entity.Member;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "comment_content")
    private String commentContent;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id" , nullable = false)
    private Member member;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="post_id", nullable = false)
    private Post post;

}
