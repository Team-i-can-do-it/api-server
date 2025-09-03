package com.icando.referenceMaterial.repository;

import com.icando.referenceMaterial.entity.ReferenceMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReferenceMaterialRepository extends JpaRepository<ReferenceMaterial, Long> {
    @Modifying
    @Query("DELETE FROM ReferenceMaterial rm WHERE rm.createdAt <= :cutoffDate") // TODO: bookmark 엔티티 추가후 bookmarkCount 조건 추가
    void deleteAllByBookmarkCountAndPastDays(long bookmarkCount, LocalDateTime cutoffDate);
}
