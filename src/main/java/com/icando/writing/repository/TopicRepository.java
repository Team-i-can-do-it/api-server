package com.icando.writing.repository;

import com.icando.writing.entity.Topic;
import com.icando.writing.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// TODO: 추후 랜덤 성능 개선
public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {
    @Query("SELECT distinct t FROM Topic t WHERE SIZE(t.referenceMaterials) < :referenceMaterialsCount and t.category = 'CULTURE_ARTS'") // TODO: 임시로 문화예술 카테고리만
    List<Topic> findByReferenceMaterialsCount(int referenceMaterialsCount);

    List<Topic> findAllByCategory(Category category);
}
