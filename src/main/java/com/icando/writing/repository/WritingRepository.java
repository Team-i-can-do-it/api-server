package com.icando.writing.repository;

import com.icando.member.entity.Member;
import com.icando.writing.entity.Writing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WritingRepository extends JpaRepository<Writing, Long> {
    @Query("""
        SELECT w FROM Writing w
            JOIN FETCH w.topic t
            JOIN FETCH w.feedback f
        WHERE w.member = :member
    """)
    Page<Writing> findAllByMember(Member member, Pageable pageable);

    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
