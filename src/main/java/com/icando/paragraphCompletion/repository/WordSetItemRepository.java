package com.icando.paragraphCompletion.repository;

import com.icando.paragraphCompletion.entity.WordSetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordSetItemRepository extends JpaRepository<WordSetItem, Integer> {
    @Query(value = "SELECT * FROM word_set_item ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<WordSetItem> getRandomWords(int count);
}
