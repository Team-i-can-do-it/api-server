package com.icando.member.repository;

import com.icando.member.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory,Long> {
    int countByMemberIdAndCreatedAt(Long memberId, LocalDate today);
}
