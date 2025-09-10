package com.icando.bookmark.entity;

import com.icando.global.BaseEntity;
import com.icando.member.entity.Member;
import com.icando.referenceMaterial.entity.ReferenceMaterial;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReferenceMaterial referenceMaterial;

    public static Bookmark of (Member member, ReferenceMaterial referenceMaterial) {
        Bookmark bookmark = new Bookmark();
        bookmark.member = member;
        bookmark.referenceMaterial = referenceMaterial;
        return bookmark;
    }
}
