package com.icando.paragraphCompletion.repository;

import com.icando.member.entity.Member;
import com.icando.paragraphCompletion.entity.ParagraphCompletion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParagraphCompletionRepository extends JpaRepository<ParagraphCompletion,Long> {
    @Query("""
            SELECT pc FROM ParagraphCompletion pc
            JOIN FETCH pc.member m
            JOIN FETCH pc.paragraphWords pw
            LEFT JOIN FETCH pc.feedback f
            LEFT JOIN FETCH pc.feedback.feedbackScore fs
            WHERE pc.id = :id AND m = :member
    """)
    Optional<ParagraphCompletion> findByIdAndMember(Long id, Member member);

    @EntityGraph(attributePaths = {"member", "paragraphWords"})
    @Query("""
        SELECT pc FROM ParagraphCompletion pc
        WHERE pc.member = :member
""")
    Page<ParagraphCompletion> findAllByMember(Member member, Pageable pageable);

    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
