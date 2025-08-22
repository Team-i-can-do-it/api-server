package com.icando.paragraphCompletion.repository;

import com.icando.paragraphCompletion.entity.WordSetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordSetItemRepository extends JpaRepository<WordSetItem, Integer> {
    @Query(value = """
    SELECT * FROM word_set_item
    WHERE id >= (SELECT FLOOR(RANDOM() * (SELECT MAX(id) - MIN(id) + 1 FROM word_set_item) + (SELECT MIN(id) FROM word_set_item)))
        AND id not in (:ids)
    ORDER BY id
    LIMIT 1
    """, nativeQuery = true)
    WordSetItem getRandomWord(List<Integer> ids);
}
