package com.icando.paragraphCompletion.repository;

import com.icando.member.entity.Member;
import com.icando.paragraphCompletion.entity.ParagraphCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParagraphCompletionRepository extends JpaRepository<ParagraphCompletion,Long> {
    Optional<ParagraphCompletion> findByIdAndMember(Long id, Member member);
}
