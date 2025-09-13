package com.icando.member.repository;

import com.icando.member.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory,Long> {

    int countByMemberIdAndCreatedAt(Long memberId, LocalDate today);
    List<PointHistory> findAllByMemberId(Long memberId);
    List<PointHistory> findByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    void deleteAllByMemberId(@Param("memberId") Long memberId);

}
