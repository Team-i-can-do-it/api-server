package com.icando.paragraphCompletion.repository;

import com.icando.paragraphCompletion.entity.ParagraphWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParagraphWordRepository extends JpaRepository<ParagraphWord, Long> {
}
