package com.icando.bookmark.repository;

import com.icando.bookmark.entity.Bookmark;
import com.icando.member.entity.Member;
import com.icando.referenceMaterial.entity.ReferenceMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByMemberAndReferenceMaterial(Member member, ReferenceMaterial referenceMaterial);

    @Query("""
SELECT b FROM Bookmark b
JOIN FETCH b.referenceMaterial rm
WHERE b.member = :member
""")
    Page<Bookmark> findAllByMember(Member member, Pageable pageable);

    Optional<Bookmark> findBookmarkByIdAndMember(Long id, Member member);

    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
