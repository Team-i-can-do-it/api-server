package com.icando.paragraphCompletion.repository;

import com.icando.paragraphCompletion.entity.ParagraphCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParagraphCompletionRepository extends JpaRepository<ParagraphCompletion,Long> {
}
