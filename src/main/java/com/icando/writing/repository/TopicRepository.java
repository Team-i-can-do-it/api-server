package com.icando.writing.repository;

import com.icando.writing.entity.Topic;
import com.icando.writing.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// TODO: 추후 랜덤 성능 개선
public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Query(value = "SELECT * FROM topic t WHERE t.category = :category ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Topic> findRandomByCategory(@Param("category") String category);

    @Query(value = "SELECT * FROM topic ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Topic> findRandom();

    @Query("SELECT distinct t FROM Topic t WHERE SIZE(t.referenceMaterials) < :referenceMaterialsCount and t.category = 'CULTURE_ARTS'") // TODO: 임시로 문화예술 카테고리만
    List<Topic> findByReferenceMaterialsCount(int referenceMaterialsCount);
}
