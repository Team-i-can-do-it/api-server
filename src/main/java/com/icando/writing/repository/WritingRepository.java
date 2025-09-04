package com.icando.writing.repository;

import com.icando.member.entity.Member;
import com.icando.writing.entity.Writing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WritingRepository extends JpaRepository<Writing, Long> {
    Page<Writing> findAllByMember(Member member, Pageable pageable);
}
